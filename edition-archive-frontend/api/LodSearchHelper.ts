import type { LocationQuery } from 'vue-router';
import { buildSearchQuery, useMyCoReFindEndpoint } from '~/api/SearchQuery';

export const LodFilterParams = [
  'objectKind:mycoreobject',
  'objectType:mods',
  'state:published',
  'objectProject:lod',
  "NOT(category.top:mir_genres\\:edition)"
];

export const LodParams = [
  'wt=json',
  'indent=true',
  'sort=id asc',
  'facet=true'
];

export enum TranslationMode {
  ALL = "ALL",
  ORIGINAL_ONLY = "ORIGINAL_ONLY",
  TRANSLATION_ONLY = "TRANSLATION_ONLY"
}

export enum ManuscriptMode {
  ALL = "ALL",
  WITH = "WITH",
  WITHOUT = "WITHOUT"
}

export interface LodFilters {
  genres: string[];
  languages: string[];
  authors: string[];
  recipients: string[];
  personEntityLinks: string[];
  orgEntityLinks: string[];
  translationMode: TranslationMode;
  manuscriptMode: ManuscriptMode;
}

// Advanced search fields
export interface LodAdvancedSearch {
  allMeta?: string;
  person?: string;
  place?: string;
  subject?: string;
  dateRangeRange?: boolean;
  dateRangeFrom?: number | string;
  dateRangeTo?: number | string;
  dateText?: string;
}

/**
 * Builds a Solr fq that matches documents whose metadata OR whose TEI/XML files
 * reference the given LOD entity URI. The file match uses the same derivate join
 * as the manuscript filter; the metadata field lives on the object itself, so it
 * is OR-ed in via the _query_ magic field.
 */
function buildEntityLinkFilter(uri: string, metadataField: string, fileField: string): string {
  const join = `{!join from=derivateID to=derivates v='objectType:data_file AND ${fileField}:\\"${uri}\\"'}`;
  return `(${metadataField}:"${uri}" OR _query_:"${join}")`;
}

function normalizeDateBoundary(value: number | string, endOfYear: boolean): string | null {
  const date = String(value).trim();
  if (/^\d{4}-\d{2}-\d{2}$/.test(date)) {
    return date;
  }
  if (/^\d{4}$/.test(date)) {
    return `${date}-${endOfYear ? '12-31' : '01-01'}`;
  }
  return null;
}

/**
 * Builds a Solr query string for advanced search fields using fq filters.
 * Returns an array of filter strings that can be appended to the URL.
 */
function buildAdvancedSearchFilters(advanced: LodAdvancedSearch): string[] {
  const filters: string[] = [];
  
  // Free text search (allMeta) - uses allMeta field
  if (advanced.allMeta && advanced.allMeta !== '*') {
    const escaped = advanced.allMeta.replace(/(["\\])/g, '\\$1');
    filters.push(`allMeta:"${escaped}"`);
  }
  
  // Person search - matches author or recipient facets
  if (advanced.person && advanced.person !== '') {
    const escaped = advanced.person.replace(/(["\\])/g, '\\$1');
    filters.push(`(ditav.mods.origin.author.facet:*${escaped}* OR ditav.mods.origin.receipient.facet:*${escaped}*)`);
  }
  
  // Place search - matches place field
  if (advanced.place && advanced.place !== '') {
    const escaped = advanced.place.replace(/(["\\])/g, '\\$1');
    filters.push(`ditav.mods.place:"${escaped}"`);
  }
  
  // Subject search - MODS topics are indexed in mods.subject
  if (advanced.subject && advanced.subject !== '') {
    if (advanced.subject === '*') {
      filters.push('mods.subject:[* TO *]');
    } else {
      const escaped = advanced.subject.replace(/(["\\])/g, '\\$1');
      filters.push(`mods.subject:"${escaped}"`);
    }
  }
  
  // Date and date range search against raw dateCreated start/end values.
  if (advanced.dateRangeRange) {
    if (advanced.dateRangeFrom && advanced.dateRangeTo) {
      const from = normalizeDateBoundary(advanced.dateRangeFrom, false);
      const to = normalizeDateBoundary(advanced.dateRangeTo, true);
      if (from && to) {
        filters.push(`ditav.mods.dateCreated.start:[${from} TO *]`);
        filters.push(`ditav.mods.dateCreated.end:[* TO ${to}]`);
      }
    }
  } else if (advanced.dateRangeFrom) {
    const date = normalizeDateBoundary(advanced.dateRangeFrom, false);
    if (date && /^\d{4}-\d{2}-\d{2}$/.test(String(advanced.dateRangeFrom).trim())) {
      filters.push(`ditav.mods.dateCreated.start:"${date}"`);
      filters.push(`ditav.mods.dateCreated.end:"${date}"`);
    } else if (date) {
      const end = normalizeDateBoundary(advanced.dateRangeFrom, true);
      filters.push(`ditav.mods.dateCreated.start:[${date} TO ${end}]`);
      filters.push(`ditav.mods.dateCreated.end:[${date} TO ${end}]`);
    }
  }
  
  // Date text search (displayDate field)
  if (advanced.dateText && advanced.dateText !== '') {
    const escaped = advanced.dateText.replace(/(["\\])/g, '\\$1');
    filters.push(`ditav.mods.displayDate:"${escaped}"`);
  }
  
  return filters;
}

export function buildLodSearchRequestURL(url: string, search: string | null, filters: LodFilters, start: number, rows = 20, advanced?: LodAdvancedSearch) {
  const urlObj = new URL(url);

  urlObj.search = '';
  useMyCoReFindEndpoint(urlObj);

  const escaped = search?.replace(/(["\\])/g, '\\$1');
  const queryTerm = escaped && escaped !== '*' ? `"${escaped}"` : '*';
  const searchTerm = buildSearchQuery(queryTerm);
  urlObj.searchParams.set('condQuery', searchTerm);
  urlObj.searchParams.set('initialCondQuery', searchTerm);
  urlObj.searchParams.set('rows', rows.toString());
  urlObj.searchParams.set('start', start.toString());

  for (const param of LodParams) {
    const [key, value] = param.split('=');
    urlObj.searchParams.set(key, value ?? 'true');
  }

  urlObj.searchParams.append('facet.field', 'category.top');
  urlObj.searchParams.append('facet.field', 'ditav.mods.origin.author.facet');
  urlObj.searchParams.append('facet.field', 'ditav.mods.origin.receipient.facet');
  urlObj.searchParams.append('fq', LodFilterParams.join(' AND '));

  if (filters?.genres?.length > 0) {
    const genreFilters = filters.genres.map(g => `"lod_document_classification:${g}"`);
    urlObj.searchParams.append('fq', `category.top:(${genreFilters.join(' AND ')})`);
  }

  if (filters?.languages?.length > 0) {
    for (const language of filters.languages) {
      urlObj.searchParams.append('fq', `category.top:"rfc5646:${language}"`);
    }
  }

  if(filters?.authors?.length > 0) {
    urlObj.searchParams.append('fq', `ditav.mods.origin.author.facet:(${filters.authors.map((aName=> `"${aName}"`)).join(' OR ')})`);
  }

  if(filters?.recipients?.length > 0) {
    urlObj.searchParams.append('fq', `ditav.mods.origin.receipient.facet:(${filters.recipients.map((aName=> `"${aName}"`)).join(' OR ')})`);
  }

  for (const uri of filters?.personEntityLinks ?? []) {
    urlObj.searchParams.append('fq', buildEntityLinkFilter(uri, 'ditav.mods.dante_metadata_pers_link', 'ditav.mods.dante_file_pers_link'));
  }

  for (const uri of filters?.orgEntityLinks ?? []) {
    urlObj.searchParams.append('fq', buildEntityLinkFilter(uri, 'ditav.mods.dante_metadata_org_link', 'ditav.mods.dante_file_org_link'));
  }

  // Add advanced search filters
  if (advanced) {
    const advancedFilters = buildAdvancedSearchFilters(advanced);
    for (const f of advancedFilters) {
      urlObj.searchParams.append('fq', f);
    }
  }

  switch (filters.translationMode) {
    case TranslationMode.ALL:
      break;
    case TranslationMode.TRANSLATION_ONLY:
      urlObj.searchParams.append('fq', 'category.top:"translation\\:yes"');
      break;
    case TranslationMode.ORIGINAL_ONLY:
      urlObj.searchParams.append('fq', 'NOT(category.top:"translation\\:yes")');
      break;
  }

  const manuscriptJoin = "{!join from=derivateID to=derivates v='objectType:data_file AND fileName:(*.jpg OR *.jpeg OR *.png OR *.tif OR *.tiff OR *.gif OR *.bmp OR *.webp OR *.svg)'}";
  switch (filters.manuscriptMode) {
    case ManuscriptMode.WITH:
      urlObj.searchParams.append('fq', manuscriptJoin);
      break;
    case ManuscriptMode.WITHOUT:
      urlObj.searchParams.append('fq', `-${manuscriptJoin}`);
      break;
  }

  return urlObj.toString();
}

export function lodModelToQuery(model: any): any {
  const query: any = {
    q: model.searchString,
    start: model.start.toString(),
  };

  if (model.filters.genres.length > 0) {
    query.genres = model.filters.genres.slice();
  }

  if (model.filters.languages.length > 0) {
    query.languages = model.filters.languages.slice();
  }

  if (model.filters.authors.length > 0) {
    query.authors = model.filters.authors.slice();
  }

  if (model.filters.recipients.length > 0) {
    query.recipients = model.filters.recipients.slice();
  }

  if (model.filters.personEntityLinks.length > 0) {
    query.personEntityLink = model.filters.personEntityLinks.slice();
  }

  if (model.filters.orgEntityLinks.length > 0) {
    query.orgEntityLink = model.filters.orgEntityLinks.slice();
  }

  if (model.filters.translationMode !== TranslationMode.ALL) {
    query.translationMode = model.filters.translationMode;
  }

  if (model.filters.manuscriptMode !== ManuscriptMode.ALL) {
    query.manuscriptMode = model.filters.manuscriptMode;
  }

  // Advanced search fields
  if (model.advanced) {
    const adv = model.advanced;
    if (adv.allMeta && adv.allMeta !== '*') query.allMeta = adv.allMeta;
    if (adv.person) query.person = adv.person;
    if (adv.place) query.place = adv.place;
    if (adv.subject) query.subject = adv.subject;
    if (adv.dateRangeRange !== undefined) query.dateRangeRange = adv.dateRangeRange;
    if (adv.dateRangeFrom !== undefined && adv.dateRangeFrom !== '') query.dateRangeFrom = adv.dateRangeFrom;
    if (adv.dateRangeTo !== undefined && adv.dateRangeTo !== '') query.dateRangeTo = adv.dateRangeTo;
    if (adv.dateText) query.dateText = adv.dateText;
  }

  return query;
}

export function lodQueryToModel(query: LocationQuery, model: any) {
  model.searchString = (query.q as string) || '*';
  model.start = query.start ? parseInt(query.start as string) : 0;

  if (query.genres) {
    model.filters.genres = Array.isArray(query.genres) ? [...query.genres as string[]] : [query.genres as string];
  } else {
    model.filters.genres = [];
  }

  if (query.languages) {
    model.filters.languages = Array.isArray(query.languages) ? [...query.languages as string[]] : [query.languages as string];
  } else {
    model.filters.languages = [];
  }

  if(query.authors) {
    model.filters.authors = Array.isArray(query.authors) ? [...query.authors as string[]] : [query.authors as string];
  } else {
    model.filters.authors = [];
  }

  if(query.recipients) {
    model.filters.recipients = Array.isArray(query.recipients) ? [...query.recipients as string[]] : [query.recipients as string];
  } else {
    model.filters.recipients = [];
  }

  if(query.personEntityLink) {
    model.filters.personEntityLinks = Array.isArray(query.personEntityLink) ? [...query.personEntityLink as string[]] : [query.personEntityLink as string];
  } else {
    model.filters.personEntityLinks = [];
  }

  if(query.orgEntityLink) {
    model.filters.orgEntityLinks = Array.isArray(query.orgEntityLink) ? [...query.orgEntityLink as string[]] : [query.orgEntityLink as string];
  } else {
    model.filters.orgEntityLinks = [];
  }

  if (query.translationMode) {
    model.filters.translationMode = query.translationMode as TranslationMode;
  }

  model.filters.manuscriptMode = (query.manuscriptMode as ManuscriptMode) || ManuscriptMode.ALL;

  // Advanced search fields
  if (!model.advanced) {
    model.advanced = {};
  }
  
  if (query.allMeta) {
    model.advanced.allMeta = query.allMeta as string;
  }
  if (query.person) {
    model.advanced.person = query.person as string;
  }
  if (query.place) {
    model.advanced.place = query.place as string;
  }
  if (query.subject) {
    model.advanced.subject = query.subject as string;
  }
  if (query.dateRangeRange !== undefined) {
    const rangeVal = query.dateRangeRange as string;
    model.advanced.dateRangeRange = rangeVal === 'true' || rangeVal === '1';
  }
  if (query.dateRangeFrom !== undefined && query.dateRangeFrom !== '') {
    model.advanced.dateRangeFrom = query.dateRangeFrom;
  }
  if (query.dateRangeTo !== undefined && query.dateRangeTo !== '') {
    model.advanced.dateRangeTo = query.dateRangeTo;
  }
  if (query.dateText) {
    model.advanced.dateText = query.dateText as string;
  }
}

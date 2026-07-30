import type { LocationQuery } from 'vue-router';

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

export function buildLodSearchRequestURL(url: string, search: string | null, filters: LodFilters, start: number, rows = 20) {
  const urlObj = new URL(url);

  urlObj.search = '';

  const escaped = search?.replace(/(["\\])/g, '\\$1');
  const queryTerm = escaped && escaped !== '*' ? `"${escaped}"` : '*';
  urlObj.searchParams.set('q', `allMeta:${queryTerm}`);
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

}

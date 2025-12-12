import type { LocationQuery } from 'vue-router';

export const GazinFilterParams = [
  'objectKind:mycoreobject',
  'objectType:mods',
  'state:published',
  'objectProject:gzn',
  "mods.genre:source_material",
  "category.top:\"rfc5646:ku\""
];

export const GazinParams = [
  'wt=json',
  'indent=true',
  'sort=mods.identifier.type.intern asc',
  'facet=true'
];

export interface GazinFilters {
  genres: string[];
  translations: string[];
}

export function buildGazinSearchRequestURL(url: string, search: string | null, filters: GazinFilters, start: number, rows = 20) {
  const urlObj = new URL(url);

  urlObj.search = '';

  urlObj.searchParams.set('q', `allMeta:${search || '*'}`);
  urlObj.searchParams.set('rows', rows.toString());
  urlObj.searchParams.set('start', start.toString());

  for (const param of GazinParams) {
    const [key, value] = param.split('=');
    urlObj.searchParams.set(key, value ?? 'true');
  }

  urlObj.searchParams.append('facet.field', 'category.top');
  urlObj.searchParams.append('fq', GazinFilterParams.join(' AND '));

  if (filters?.genres?.length > 0) {
    urlObj.searchParams.append('fq', `category.top:(${filters.genres.map((gName=> `"gazin_genres:${gName}"`)).join(' AND ')})`);
  }

  if(filters?.translations?.length > 0) {
    urlObj.searchParams.append('fq', `category.top:(${filters.translations.map((gName=> `"translation_available:${gName}"`)).join(' AND ')})`);
  }

  return urlObj.toString();
}

export function gazinModelToQuery(model: any): any {
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

  if (model.filters.translations.length > 0) {
    query.translations = model.filters.translations.slice();
  }

  return query;
}

export function gazinQueryToModel(query: LocationQuery, model: any) {
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

  if (query.translations) {
    model.filters.translations = Array.isArray(query.translations) ? [...query.translations as string[]] : [query.translations as string];
  } else {
    model.filters.translations = [];
  }
}

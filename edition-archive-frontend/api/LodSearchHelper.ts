import type { LocationQuery } from 'vue-router';

export const LodFilterParams = [
  'objectKind:mycoreobject',
  'objectType:mods',
  'state:published',
  'objectProject:lod',
  "mods.genre:letter"
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

export interface LodFilters {
  genres: string[];
  languages: string[];
  translationMode: TranslationMode;
}

export function buildLodSearchRequestURL(url: string, search: string | null, filters: LodFilters, start: number, rows = 20) {
  const urlObj = new URL(url);

  urlObj.search = '';

  urlObj.searchParams.set('q', `allMeta:${search || '*'}`);
  urlObj.searchParams.set('rows', rows.toString());
  urlObj.searchParams.set('start', start.toString());

  for (const param of LodParams) {
    const [key, value] = param.split('=');
    urlObj.searchParams.set(key, value ?? 'true');
  }

  urlObj.searchParams.append('facet.field', 'mods.genre');
  urlObj.searchParams.append('facet.field', 'category');
  urlObj.searchParams.append('fq', LodFilterParams.join(' AND '));

  if (filters?.genres?.length > 0) {
    urlObj.searchParams.append('fq', `mods.genre:(${filters.genres.join(' AND ')})`);
  }

  if (filters?.languages?.length > 0) {
    for (const language of filters.languages) {
      urlObj.searchParams.append('fq', `category:"rfc5646:${language}"`);
    }
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
}

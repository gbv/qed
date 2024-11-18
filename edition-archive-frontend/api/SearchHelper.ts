import {LocationQuery} from "vue-router";

export const SoSuFilterParams = [
  "objectKind:mycoreobject",
  "objectType:mods",
  "state:published"
];

export const SoSuParams = [
  "wt=json",
  "indent=true",
  "sort=id asc",
];

export enum TranslationMode {
  ALL = "ALL",
  ORIGINAL_ONLY = "ORIGINAL_ONLY",
  TRANSLATION_ONLY = "TRANSLATION_ONLY"
}

export interface Filters {
  genres: string[];
  languages: string[];
  translationMode: TranslationMode;
}

export function buildSOSUSearchRequestURL(url: string, search: string | null, filters: Filters, start: number, rows = 20) {
  const query = `allMeta:${search || "*"}`
  const genreFilter = filters?.genres?.length > 0 ? `fq=mods.genre:(${filters.genres.join("%20AND%20")})` : '';
  const languageFilter = filters?.languages?.length > 0 ? `fq=survivors.mods.language:${filters.languages.join("%20AND%20")}` : '';
  let translationFilter = '';
  switch (filters.translationMode) {
    case TranslationMode.ALL:
      // empty filter
      break;
    case TranslationMode.TRANSLATION_ONLY:
      translationFilter = "fq=mods.relatedItem.original:*";
      break;
    case TranslationMode.ORIGINAL_ONLY:
      translationFilter = "fq=NOT%28mods.relatedItem.original:*%29";
      break;
  }
  let filtersParams = [genreFilter, languageFilter, translationFilter, ...SoSuParams].filter(s=>s.length>0).join("&");
  return `${url}mir/select?q=${query}&fq=${SoSuFilterParams.join("%20AND%20")}&${filtersParams}&start=${start}&rows=${rows}&facet=true&facet.field=survivors.mods.language`;
}


export function modelToQuery(model: any): any {
  const query: any = {
    q: model.searchString,
  }


  query.start = model.start.toString();


  if (model.filters.genres.length > 0) {
    query.genres = model.filters.genres.slice();
  }

  if (model.filters.languages.length > 0) {
    query.languages = model.filters.languages;
  }

  if (model.filters.translationMode !== TranslationMode.ALL) {
    query.translationMode = model.filters.translationMode;
  }

  return query;
}

 export function queryToModel(query: LocationQuery, model:any) {
  model.searchString = query.q as string || "*";
  if (query.start) {
    model.start = parseInt(query.start as string);
  } else {
    model.start = 0;
  }

  if (query.genres) {
    if (Array.isArray(query.genres)) {
      model.filters.genres = query.genres as string[];
    } else {
      model.filters.genres = [query.genres as string];
    }
  }

  if (query.languages) {
    if (Array.isArray(query.languages)) {
      model.filters.languages = query.languages as string[];
    } else {
      model.filters.languages = [query.languages as string];
    }
  }

  if (query.translationMode) {
    model.filters.translationMode = query.translationMode as TranslationMode;
  }
}


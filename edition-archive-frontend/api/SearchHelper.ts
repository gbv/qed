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
  console.log(["buildSOSUSearchRequestURL", url, search, filters, start, rows]);
  const query = `allMeta:${search || "*"}`
  const genreFilter = filters?.genres?.length > 0 ? `fq=mods.genre:(${filters.genres.join("%20AND%20")})` : '';
  const languageFilter = filters?.languages?.length > 0 ? `fq=survivors.mods.language:${filters.languages.join("%20AND%20")}` : '';
  let translationFilter = '';
  switch (filters.translationMode) {
    case TranslationMode.ALL:
      // empty filter
      break;
    case TranslationMode.ORIGINAL_ONLY:
      translationFilter = "fq=mods.relatedItem.original:*";
      break;
    case TranslationMode.TRANSLATION_ONLY:
      translationFilter = "fq=NOT%28mods.relatedItem.original:*%29";
      break;
  }
  let filtersParams = [genreFilter, languageFilter, translationFilter, ...SoSuParams].filter(s=>s.length>0).join("&");
  return `${url}mir/select?q=${query}&fq=${SoSuFilterParams.join("%20AND%20")}&${filtersParams}&start=${start}&rows=${rows}&facet=true&facet.field=survivors.mods.language`;
}
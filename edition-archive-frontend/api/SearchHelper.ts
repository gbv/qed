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

export function buildSOSUSearchRequestURL(url: string, search: string | null, start: number, rows = 20) {
  const query = `allMeta:${search || "*"}`
  return `${url}mir/select?q=${query}&fq=${SoSuFilterParams.join("%20AND%20")}&start=${start}&${SoSuParams.join("&")}&rows=${rows}`;
}
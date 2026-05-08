import type {InjectionKey} from "vue";
import type {JSKOSEntity} from "~/api/jskos";

export interface DanteEntityPaths {
  searchBasePath?: string;
  personIndexPath?: string;
  organisationIndexPath?: string;
}

export const DanteEntityPathsKey: InjectionKey<DanteEntityPaths> = Symbol('DanteEntityPaths');

export const knownIdentifierPatterns: { pattern: RegExp; label: string }[] = [
  {pattern: /^https?:\/\/d-nb\.info\/gnd\//, label: 'GND'},
  {pattern: /^https?:\/\/portal\.dnb\.de\//, label: 'GND'},
  {pattern: /^https?:\/\/viaf\.org\/viaf\//, label: 'VIAF'},
  {pattern: /^https?:\/\/www\.idref\.fr\//, label: 'IDREF'},
  {pattern: /^https?:\/\/www\.wikidata\.org\//, label: 'Wikidata'},
  {pattern: /^https?:\/\/isni\.org\//, label: 'ISNI'},
  {pattern: /^https?:\/\/orcid\.org\//, label: 'ORCID'},
  {pattern: /^https?:\/\/\w+\.wikipedia\.org\//, label: 'Wikipedia'},
  {pattern: /^https?:\/\/dbe\.rah\.es\//, label: 'DBE'},
];

export function getMappingLinks(entity: JSKOSEntity): { href: string; label: string }[] {
  if (!entity.mappings) return [];
  return entity.mappings
    .map((mapping) => mapping.to?.memberSet?.[0]?.uri)
    .filter((uri): uri is string => !!uri)
    .map((uri) => {
      const known = knownIdentifierPatterns.find((p) => p.pattern.test(uri));
      return {href: uri, label: known?.label || new URL(uri).hostname};
    });
}

export function getSearchLink(entity: JSKOSEntity, searchBasePath: string | undefined): string | undefined {
  if (!searchBasePath) return undefined;
  if (!entity.uri) return searchBasePath;
  try {
    const url = new URL(entity.uri);
    const uriPath = url.pathname.replace(/^\//, '');
    return `${searchBasePath}?q=${encodeURIComponent(uriPath)}`;
  } catch {
    return searchBasePath;
  }
}

export function useDanteEntity(danteUri: Ref<string | undefined>, entityType?: Ref<'person' | 'organisation' | undefined>) {
  const {locale} = useI18n();
  const paths = inject(DanteEntityPathsKey, {});

  const resolvedSkos = ref<JSKOSEntity | null | undefined>(null);

  let currentRequestId = 0;

  const resolve = async (uri: string | undefined) => {
    const requestId = ++currentRequestId;
    if (!uri) {
      resolvedSkos.value = undefined;
      return;
    }
    resolvedSkos.value = null;
    try {
      const response = await fetch(`https://api.dante.gbv.de/data?uri=${uri}&properties=*`);
      if (requestId !== currentRequestId) return;
      if (!response.ok) {
        resolvedSkos.value = undefined;
        return;
      }
      const data = await response.json() as JSKOSEntity[];
      if (requestId !== currentRequestId) return;
      resolvedSkos.value = data[0] ?? undefined;
    } catch {
      if (requestId !== currentRequestId) return;
      resolvedSkos.value = undefined;
    }
  };

  watch(danteUri, resolve, {immediate: true});

  const preferredLabel = computed(() => {
    if (!resolvedSkos.value?.prefLabel) return '';
    const prefLabel = resolvedSkos.value.prefLabel;
    return prefLabel[locale.value] || prefLabel['de'] || prefLabel['en'] || Object.values(prefLabel)[0] || '';
  });

  const firstDefinition = computed(() => {
    const definition = resolvedSkos.value?.definition;
    if (!definition) return '';
    const candidate = definition[locale.value] || definition['de'] || definition['en'] || Object.values(definition)[0];
    if (!candidate) return '';
    return Array.isArray(candidate) ? (candidate[0] ?? '') : candidate;
  });

  const longerDefinition = computed(() => {
    const definition = resolvedSkos.value?.definition;
    if (!definition) return '';
    const candidate = definition[locale.value] || definition['de'] || definition['en'] || Object.values(definition)[0];
    if (!candidate) return '';
    if (!Array.isArray(candidate)) return candidate;
    return candidate[1] ?? candidate[0] ?? '';
  });

  const identifierLinks = computed(() => {
    if (!resolvedSkos.value) return [];
    return getMappingLinks(resolvedSkos.value);
  });

  const searchLink = computed(() => {
    if (!paths.searchBasePath) return undefined;
    const uri = danteUri.value;
    if (!uri) return paths.searchBasePath;
    try {
      const url = new URL(uri);
      const uriPath = url.pathname.replace(/^\//, '');
      return `${paths.searchBasePath}?q=${encodeURIComponent(uriPath)}`;
    } catch {
      return paths.searchBasePath;
    }
  });

  const resolvedEntityType = computed((): 'person' | 'organisation' | undefined => {
    if (entityType?.value) return entityType.value;
    const uri = resolvedSkos.value?.uri || danteUri.value || '';
    if (uri.includes('lod_organisations')) return 'organisation';
    if (uri.includes('lod_persons')) return 'person';
    return undefined;
  });

  const indexLink = computed(() => {
    const t = resolvedEntityType.value;
    if (!t) return undefined;
    const basePath = t === 'person' ? paths.personIndexPath : paths.organisationIndexPath;
    if (!basePath) return undefined;
    const uri = resolvedSkos.value?.uri || danteUri.value;
    const anchor = uri?.split('/').pop();
    return basePath + (anchor ? `#${anchor}` : '');
  });

  const indexTranslationKey = computed(() => {
    const t = resolvedEntityType.value;
    if (t === 'person') return 'register.person';
    if (t === 'organisation') return 'register.organisation';
    return undefined;
  });

  const loading = computed(() => resolvedSkos.value === null);
  const resolved = computed(() => resolvedSkos.value != null && resolvedSkos.value !== undefined);

  return {
    resolvedSkos,
    preferredLabel,
    firstDefinition,
    longerDefinition,
    identifierLinks,
    searchLink,
    indexLink,
    indexTranslationKey,
    loading,
    resolved,
  };
}

import type {JSKOSEntity} from "~/api/jskos";

const knownIdentifierPatterns: { pattern: RegExp; label: string }[] = [
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

export interface DanteEntityType {
  key: 'person' | 'organisation';
  indexPath: string;
  indexTranslationKey: string;
}

const entityTypes: Record<string, DanteEntityType> = {
  person: {key: 'person', indexPath: '/languages-of-diplomacy/indices/people', indexTranslationKey: 'lod.person.personIndex'},
  organisation: {key: 'organisation', indexPath: '/languages-of-diplomacy/indices/organisations', indexTranslationKey: 'lod.organisation.organisationIndex'},
};

export function useDanteEntity(danteUri: Ref<string | undefined>, entityType?: Ref<'person' | 'organisation'>) {
  const {locale} = useI18n();

  const resolvedSkos = ref<JSKOSEntity | null | undefined>(null);

  const resolve = async (uri: string | undefined) => {
    if (!uri) {
      resolvedSkos.value = undefined;
      return;
    }
    resolvedSkos.value = null;
    const response = await fetch(`https://api.dante.gbv.de/data?uri=${uri}&properties=*`);
    if (!response.ok) {
      resolvedSkos.value = undefined;
      return;
    }
    const data = await response.json() as JSKOSEntity[];
    resolvedSkos.value = data[0] ?? undefined;
  };

  watch(danteUri, resolve, {immediate: true});

  const preferredLabel = computed(() => {
    if (!resolvedSkos.value?.prefLabel) return '';
    const prefLabel = resolvedSkos.value.prefLabel;
    return prefLabel[locale.value] || prefLabel['de'] || prefLabel['en'] || Object.values(prefLabel)[0] || '';
  });

  const identifierLinks = computed(() => {
    if (!resolvedSkos.value?.mappings) return [];
    return resolvedSkos.value.mappings
      .map((mapping) => mapping.to?.memberSet?.[0]?.uri)
      .filter((uri): uri is string => !!uri)
      .map((uri) => {
        const known = knownIdentifierPatterns.find((p) => p.pattern.test(uri));
        return {href: uri, label: known?.label || new URL(uri).hostname};
      });
  });

  const searchLink = computed(() => {
    const uri = danteUri.value;
    if (!uri) return '/languages-of-diplomacy/search/';
    try {
      const url = new URL(uri);
      const uriPath = url.pathname.replace(/^\//, '');
      return `/languages-of-diplomacy/search/?q=${encodeURIComponent(uriPath)}`;
    } catch {
      return '/languages-of-diplomacy/search/';
    }
  });

  const resolvedEntityType = computed((): DanteEntityType | undefined => {
    if (entityType?.value) return entityTypes[entityType.value];
    const uri = resolvedSkos.value?.uri || danteUri.value || '';
    if (uri.includes('lod_organisations')) return entityTypes.organisation;
    if (uri.includes('lod_persons')) return entityTypes.person;
    return undefined;
  });

  const indexLink = computed(() => {
    const config = resolvedEntityType.value;
    if (!config) return undefined;
    const uri = resolvedSkos.value?.uri || danteUri.value;
    const anchor = uri?.split('/').pop();
    return config.indexPath + (anchor ? `#${anchor}` : '');
  });

  const indexTranslationKey = computed(() => {
    return resolvedEntityType.value?.indexTranslationKey;
  });

  const loading = computed(() => resolvedSkos.value === null);
  const resolved = computed(() => resolvedSkos.value != null && resolvedSkos.value !== undefined);

  return {
    resolvedSkos,
    preferredLabel,
    identifierLinks,
    searchLink,
    indexLink,
    indexTranslationKey,
    loading,
    resolved,
  };
}

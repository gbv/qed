<template>
  <LoDLayout>

    <template #content>
      <h3>{{ $t("lod.organisation.organisationIndex") }}</h3>
      <div class="text-center" v-if="!data">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      <ul class="list-group list-group-flush no-underline" v-if="data">
        <li v-for="org in data" :key="org.uri" :id="anchorId(org)"
            class="list-group-item">
          <a href="#" :class="highlight === anchorId(org) ? 'text-secondary' : ''" v-on:click.prevent="toggleEntry(org)">
            <i class="bi bi-bank"></i>
            {{ getLabel(org) }}
            <i v-if="hasMappings(org)" class="bi bi-link-45deg text-secondary"></i>
          </a>
          <div v-if="org.uri && (loading[org.uri] || expandedData[org.uri])" class="popout text-start mt-2 position-relative">
            <a class="close-btn" href="#" v-on:click.prevent="closeEntry(org)">
              <i class="bi bi-x-circle"></i>
            </a>

            <template v-if="loading[org.uri]">
              <div class="text-center">
                <div class="spinner-border spinner-border-sm" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
              </div>
            </template>
            <template v-else-if="expandedData[org.uri]">
              <j-skos-lang-map-display
                v-if="expandedData[org.uri].prefLabel"
                :lang-map="expandedData[org.uri].prefLabel!"
                translation-key="metadata.skos.prefLabel"
              />
              <j-skos-lang-map-display
                v-if="expandedData[org.uri].altLabel"
                :lang-map="expandedData[org.uri].altLabel!"
                translation-key="metadata.skos.altLabel"
              />
              <j-skos-lang-map-display
                v-if="expandedData[org.uri].definition"
                :lang-map="expandedData[org.uri].definition!"
                translation-key="metadata.skos.definition"
              />

              <div class="link-list mt-2">
                <a :href="org.uri" target="_blank" rel="noopener noreferrer">DANTE</a>
                <template v-for="id in getMappingLinks(org)" :key="id.href">
                  <a :href="id.href" target="_blank" rel="noopener noreferrer">{{ id.label }}</a>
                </template>
                <nuxt-link :to="getSearchLink(org)">{{ $t('search.label') }}</nuxt-link>
              </div>
            </template>
          </div>
        </li>
      </ul>
    </template>

    <template #menu>
      <LoDMenu/>
    </template>

  </LoDLayout>
</template>

<script lang="ts" setup>
import type {JSKOSEntity} from "~/api/jskos";

const route = useRoute();
const highlight = computed(() => route.hash.substring(1));
const expandedData = reactive<Record<string, JSKOSEntity>>({});
const loading = reactive<Record<string, boolean>>({});

const getLabel = (entity: JSKOSEntity): string => {
  if (!entity.prefLabel) return '';
  return entity.prefLabel['zxx'] as string
    || entity.prefLabel['de'] as string
    || entity.prefLabel['en'] as string
    || Object.values(entity.prefLabel)[0] as string
    || '';
};

const {data} = await useAsyncData('lod-organisation-index', async () => {
  const response = await $fetch<JSKOSEntity[]>('https://api.dante.gbv.de/voc/lod_organisations/top?properties=prefLabel,notation,mappings&limit=10000');
  response.sort((a, b) => getLabel(a).localeCompare(getLabel(b)));
  return response;
});

const scrollToHighlight = async () => {
  if (highlight.value && data.value) {
    const entity = data.value.find(e => anchorId(e) === highlight.value);
    if (entity && entity.uri && !expandedData[entity.uri]) {
      await toggleEntry(entity);
    }
    await nextTick();
    const el = document.getElementById(highlight.value);
    if (el) {
      el.scrollIntoView({behavior: 'smooth', block: 'center'});
    }
  }
};

onMounted(scrollToHighlight);
watch(highlight, scrollToHighlight);

const anchorId = (entity: JSKOSEntity): string => {
  if (entity.uri) {
    const lastSegment = entity.uri.split('/').pop();
    if (lastSegment) return lastSegment;
  }
  return entity.notation?.[0] || '';
};

const hasMappings = (entity: JSKOSEntity): boolean => {
  return !!entity.mappings && entity.mappings.length > 0;
};

const toggleEntry = async (entity: JSKOSEntity) => {
  if (!entity.uri) return;
  if (expandedData[entity.uri] || loading[entity.uri]) {
    delete expandedData[entity.uri];
    delete loading[entity.uri];
  } else {
    loading[entity.uri] = true;
    const result = await $fetch<JSKOSEntity[]>(`https://api.dante.gbv.de/data?uri=${entity.uri}&properties=*`);
    delete loading[entity.uri];
    if (result?.[0]) {
      expandedData[entity.uri] = result[0];
    }
  }
};

const closeEntry = (entity: JSKOSEntity) => {
  if (entity.uri) {
    delete expandedData[entity.uri];
    delete loading[entity.uri];
  }
};

const knownIdentifierPatterns: {pattern: RegExp, label: string}[] = [
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

const getMappingLinks = (entity: JSKOSEntity) => {
  if (!entity.mappings) return [];
  return entity.mappings
    .map((mapping) => mapping.to?.memberSet?.[0]?.uri)
    .filter((uri): uri is string => !!uri)
    .map((uri) => {
      const known = knownIdentifierPatterns.find((p) => p.pattern.test(uri));
      return {href: uri, label: known?.label || new URL(uri).hostname};
    });
};

const getSearchLink = (entity: JSKOSEntity): string => {
  if (!entity.uri) return '/languages-of-diplomacy/search/';
  try {
    const url = new URL(entity.uri);
    const uriPath = url.pathname.replace(/^\//, '');
    return `/languages-of-diplomacy/search/?q=${encodeURIComponent(uriPath)}`;
  } catch {
    return '/languages-of-diplomacy/search/';
  }
};
</script>

<style scoped>
.popout {
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px;
  padding: 1rem;
  margin-bottom: 0.5rem;
  min-height: 5rem;
}

.link-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.link-list a {
  padding-left: 0.5rem;
  border-left: 1px solid rgba(0, 0, 0, 0.125);
}

.link-list a:first-child {
  padding-left: 0;
  border-left: none;
}

.close-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
}
</style>

<template>
  <LoDLayout>

    <template #content>
      <h3>{{ $t("lod.person.personIndex") }}</h3>
      <div class="text-center" v-if="!data">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      <ul class="list-group list-group-flush no-underline" v-if="data">
        <li v-for="person in data" :key="person.uri" :id="personAnchorId(person)"
            class="list-group-item">
          <a href="#" :class="highlight === personAnchorId(person) ? 'text-secondary' : ''" v-on:click.prevent="togglePerson(person)">
            <i class="bi bi-person"></i>
            {{ getLabel(person) }}
            <i v-if="hasMappings(person)" class="bi bi-link-45deg text-secondary"></i>
          </a>
          <div v-if="person.uri && (loadingPersons[person.uri] || expandedData[person.uri])" class="popout text-start mt-2 position-relative">
            <a class="close-btn" href="#" v-on:click.prevent="closePerson(person)">
              <i class="bi bi-x-circle"></i>
            </a>

            <template v-if="loadingPersons[person.uri]">
              <div class="text-center">
                <div class="spinner-border spinner-border-sm" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
              </div>
            </template>
            <template v-else-if="expandedData[person.uri]">
              <j-skos-lang-map-display
                v-if="expandedData[person.uri].prefLabel"
                :lang-map="expandedData[person.uri].prefLabel!"
                translation-key="metadata.skos.prefLabel"
              />
              <j-skos-lang-map-display
                v-if="expandedData[person.uri].altLabel"
                :lang-map="expandedData[person.uri].altLabel!"
                translation-key="metadata.skos.altLabel"
              />
              <j-skos-lang-map-display
                v-if="expandedData[person.uri].definition"
                :lang-map="expandedData[person.uri].definition!"
                translation-key="metadata.skos.definition"
              />

              <div class="link-list mt-2">
                <a :href="person.uri" target="_blank" rel="noopener noreferrer">DANTE</a>
                <template v-for="id in getMappingLinks(person)" :key="id.href">
                  <a :href="id.href" target="_blank" rel="noopener noreferrer">{{ id.label }}</a>
                </template>
                <nuxt-link :to="getSearchLink(person)">{{ $t('search.label') }}</nuxt-link>
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
const loadingPersons = reactive<Record<string, boolean>>({});

const getLabel = (person: JSKOSEntity): string => {
  if (!person.prefLabel) return '';
  return person.prefLabel['zxx'] as string
    || person.prefLabel['de'] as string
    || person.prefLabel['en'] as string
    || Object.values(person.prefLabel)[0] as string
    || '';
};

const {data} = await useAsyncData('lod-person-index', async () => {
  const response = await $fetch<JSKOSEntity[]>('https://api.dante.gbv.de/voc/lod_persons/top?properties=prefLabel,notation,mappings&limit=10000');
  response.sort((a, b) => getLabel(a).localeCompare(getLabel(b)));
  return response;
});

const scrollToHighlight = async () => {
  if (highlight.value && data.value) {
    const person = data.value.find(p => personAnchorId(p) === highlight.value);
    if (person && person.uri && !expandedData[person.uri]) {
      await togglePerson(person);
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

const personAnchorId = (person: JSKOSEntity): string => {
  if (person.uri) {
    const lastSegment = person.uri.split('/').pop();
    if (lastSegment) return lastSegment;
  }
  return person.notation?.[0] || '';
};

const hasMappings = (person: JSKOSEntity): boolean => {
  return !!person.mappings && person.mappings.length > 0;
};

const togglePerson = async (person: JSKOSEntity) => {
  if (!person.uri) return;
  if (expandedData[person.uri] || loadingPersons[person.uri]) {
    delete expandedData[person.uri];
    delete loadingPersons[person.uri];
  } else {
    loadingPersons[person.uri] = true;
    const result = await $fetch<JSKOSEntity[]>(`https://api.dante.gbv.de/data?uri=${person.uri}&properties=*`);
    delete loadingPersons[person.uri];
    if (result?.[0]) {
      expandedData[person.uri] = result[0];
    }
  }
};

const closePerson = (person: JSKOSEntity) => {
  if (person.uri) {
    delete expandedData[person.uri];
    delete loadingPersons[person.uri];
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

const getMappingLinks = (person: JSKOSEntity) => {
  if (!person.mappings) return [];
  return person.mappings
    .map((mapping) => mapping.to?.memberSet?.[0]?.uri)
    .filter((uri): uri is string => !!uri)
    .map((uri) => {
      const known = knownIdentifierPatterns.find((p) => p.pattern.test(uri));
      return {href: uri, label: known?.label || new URL(uri).hostname};
    });
};

const getSearchLink = (person: JSKOSEntity): string => {
  if (!person.uri) return '/languages-of-diplomacy/search/';
  try {
    const url = new URL(person.uri);
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

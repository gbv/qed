<template>
  <div class="text-center" v-if="!data">
    <div class="spinner-border" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
  <ul class="list-group list-group-flush no-underline" v-if="data">
    <li v-for="entity in data" :key="entity.uri" :id="anchorId(entity)"
        class="list-group-item">
      <a href="#" :class="highlight === anchorId(entity) ? 'text-secondary' : ''"
         v-on:click.prevent="toggleEntry(entity)">
        <i :class="`bi ${icon}`"></i>
        {{ getLabel(entity) }}
        <i v-if="hasMappings(entity)" class="bi bi-link-45deg text-secondary"></i>
      </a>
      <div v-if="entity.uri && (loading[entity.uri] || expandedData[entity.uri])"
           class="popout text-start mt-2 position-relative">
        <a class="close-btn" href="#" v-on:click.prevent="closeEntry(entity)">
          <i class="bi bi-x-circle"></i>
        </a>

        <template v-if="loading[entity.uri]">
          <div class="text-center">
            <div class="spinner-border spinner-border-sm" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
        </template>
        <template v-else-if="expandedData[entity.uri]">
          <j-skos-lang-map-display
            v-if="expandedData[entity.uri].prefLabel"
            :lang-map="expandedData[entity.uri].prefLabel!"
            translation-key="metadata.skos.prefLabel"
          />
          <j-skos-lang-map-display
            v-if="expandedData[entity.uri].altLabel"
            :lang-map="expandedData[entity.uri].altLabel!"
            translation-key="metadata.skos.altLabel"
          />
          <j-skos-lang-map-display
            v-if="expandedData[entity.uri].definition"
            :lang-map="expandedData[entity.uri].definition!"
            translation-key="metadata.skos.definition"
          />

          <div class="link-list mt-2">
            <a :href="entity.uri" target="_blank" rel="noopener noreferrer">DANTE</a>
            <template v-for="id in getMappingLinks(entity)" :key="id.href">
              <a :href="id.href" target="_blank" rel="noopener noreferrer">{{ id.label }}</a>
            </template>
            <nuxt-link v-if="searchLinkFor(entity)" :to="searchLinkFor(entity)!">{{ $t('search.label') }}</nuxt-link>
          </div>
        </template>
      </div>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import type {JSKOSEntity} from "~/api/jskos";
import {DanteEntityPathsKey, getMappingLinks, getSearchLink} from "~/composables/DanteEntity";

const props = defineProps<{
  vocabularyId: string;
  icon: string;
}>();

const paths = inject(DanteEntityPathsKey, {});

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

const {data} = await useAsyncData(`dante-vocab-index-${props.vocabularyId}`, async () => {
  const pageSize = 1000;
  const byUri = new Map<string, JSKOSEntity>();
  const extras: JSKOSEntity[] = [];
  for (let offset = 0; ; offset += pageSize) {
    const page = await $fetch<JSKOSEntity[]>(`https://api.dante.gbv.de/voc/${props.vocabularyId}/top?properties=prefLabel,notation,mappings&offset=${offset}`);
    for (const entity of page) {
      if (entity.uri) {
        if (!byUri.has(entity.uri)) byUri.set(entity.uri, entity);
      } else {
        extras.push(entity);
      }
    }
    if (page.length < pageSize) break;
  }
  const all = [...byUri.values(), ...extras];
  all.sort((a, b) => getLabel(a).localeCompare(getLabel(b)));
  return all;
});

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

const searchLinkFor = (entity: JSKOSEntity) => getSearchLink(entity, paths.searchBasePath);

const toggleEntry = async (entity: JSKOSEntity) => {
  if (!entity.uri) return;
  if (expandedData[entity.uri] || loading[entity.uri]) {
    delete expandedData[entity.uri];
    delete loading[entity.uri];
  } else {
    loading[entity.uri] = true;
    try {
      const result = await $fetch<JSKOSEntity[]>(`https://api.dante.gbv.de/data?uri=${entity.uri}&properties=*`);
      if (result?.[0]) {
        expandedData[entity.uri] = result[0];
      }
    } finally {
      delete loading[entity.uri];
    }
  }
};

const closeEntry = (entity: JSKOSEntity) => {
  if (entity.uri) {
    delete expandedData[entity.uri];
    delete loading[entity.uri];
  }
};

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

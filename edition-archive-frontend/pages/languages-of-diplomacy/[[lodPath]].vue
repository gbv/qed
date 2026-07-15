<template>
  <LoDLayout>

    <template #content>
      <CMSPage default-language="en" :languages="['de', 'en', 'fr', 'ru']" :slug="`/qed${path}`"/>

      <div v-if="route.path === '/languages-of-diplomacy/' && mapResponse" class="row mt-4">
        <div class="col-12">
          <client-only>
            <MapWithMetadata
              ref="lodMap"
              :center-x="20"
              :center-y="52"
              wkt-field="ditav.tei.coordinates"
              :solr-response="mapResponse"
              v-on:select="loadDocuments">
              <template #metadata="{ solrdocs }">
                <div class="card documents_card m-3">
                  <div class="card-header">
                    <div class="text-end">
                      <span class="bi bi-x clickable" v-on:click="unselectAll" :title="$t('button.close')"></span>
                    </div>
                  </div>
                  <div class="card-body">
                    <div
                      class="row document mt-3"
                      v-for="doc in distinctDocuments(solrdocs)" :key="doc.returnId">
                      <div class="col-12">
                        <h3 class="text-center">
                          <nuxt-link
                            :to="`/languages-of-diplomacy/documents/${getMyCoReIdNumber(doc.returnId)}`"
                            class="main-title">
                            {{ docTitle(doc.returnId) }}
                          </nuxt-link>
                        </h3>
                      </div>
                      <div v-if="hasDerivate(doc.returnId)" class="col-12 text-center mt-1">
                        <IiifThumbnail :app-url="ditavURL" :id="doc.returnId" />
                      </div>
                      <div v-if="docSubtitle(doc.returnId)" class="col-12 mt-1 text-center subtitle">
                        {{ docSubtitle(doc.returnId) }}
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </MapWithMetadata>
          </client-only>
        </div>
      </div>
    </template>

  </LoDLayout>
</template>

<script lang="ts" setup>

import {getMyCoReIdNumber} from "~/api/MyCoRe";
import {
  buildLodMapDataFileURL, buildLodObjectsURL, lodDocumentSubtitle, lodDocumentTitle
} from "~/api/LodMapHelper";

const route = useRoute();
const path = route.path;

const {$ditavSolrURL, $ditavURL} = useNuxtApp();
const ditavSolrURL = $ditavSolrURL();
const ditavURL = $ditavURL();

const mapResponse = ref(null as any);
// returnId -> mods solr doc; filled lazily when a cluster is selected
const docMap = reactive({} as Record<string, any>);

// initial page load only needs the marker coordinates; object data is fetched on demand
if (route.path === '/languages-of-diplomacy/') {
  const {data} = await useAsyncData('lod-map-data', async () => {
    const resp = await $fetch(buildLodMapDataFileURL(ditavSolrURL));
    return typeof resp === 'string' ? JSON.parse(resp) : resp;
  });
  mapResponse.value = data.value;
}

// fetch object docs (titles/subtitles) for just the selected cluster's docs, skipping ones
// already cached. Chunked because a busy place can cluster more docs than fit in one request
// URL (HTTP 414). Thumbnails check their own existence inside <IiifThumbnail>.
const loadDocuments = async (solrdocs: any[]) => {
  const ids = [...new Set(solrdocs.map((d) => d.returnId).filter(Boolean))]
    .filter((id) => !(id in docMap)) as string[];
  if (ids.length === 0) {
    return;
  }

  const chunkSize = 80;
  for (let i = 0; i < ids.length; i += chunkSize) {
    const chunk = ids.slice(i, i + chunkSize);
    const resp = await $fetch(buildLodObjectsURL(ditavSolrURL, chunk));
    const json = typeof resp === 'string' ? JSON.parse(resp) : resp;
    for (const doc of (json?.response?.docs ?? []) as any[]) {
      docMap[doc.id] = doc;
    }
  }
};

const distinctDocuments = (solrdocs: any[]) => {
  const seen = new Set<string>();
  const result: any[] = [];
  for (const doc of solrdocs) {
    if (doc.returnId && !seen.has(doc.returnId)) {
      seen.add(doc.returnId);
      result.push(doc);
    }
  }
  return result;
};

const docTitle = (returnId: string) => docMap[returnId] ? lodDocumentTitle(docMap[returnId]) : returnId;
const docSubtitle = (returnId: string) => docMap[returnId] ? lodDocumentSubtitle(docMap[returnId]) : '';
// cheap pre-filter: skip the thumbnail (and its info.json probe) for docs without a derivate
const hasDerivate = (returnId: string) => (docMap[returnId]?.derCount ?? 0) > 0;

const lodMap = useTemplateRef('lodMap');
const unselectAll = () => {
  if (lodMap.value) {
    lodMap.value.unselectAll();
  }
};

</script>

<style scoped>

.clickable {
  cursor: pointer;
}

.documents_card {
  height: 350px !important;
}

.documents_card .card-body {
  overflow: scroll;
}

.document:not(:last-child) {
  border-bottom: 1px solid;
  border-image: linear-gradient(to right, transparent, #006978, transparent) 1;
  padding-bottom: 2rem;
  margin-bottom: 2rem;
}

.subtitle {
  font-style: italic;
}

</style>

<style>
.ol-overlay-container {
  width: 30% !important;
  min-width: 300px !important;
}
</style>

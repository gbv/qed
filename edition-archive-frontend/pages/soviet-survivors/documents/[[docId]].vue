<template>
  <SovietSurvivorsLayout>

    <template #content>
      <div class="row">

        <div class="col-12 sosu-detail-view__navigation">
          <div class="top-nav d-flex row">
            <div class="col-5 text-start">
              <nuxt-link :to="data?.prev?.link"
                         v-if="data?.prev">
                <i class="bi bi-chevron-left"></i>
                <span>{{ data?.prev?.title }}</span>
              </nuxt-link>
            </div>
            <div class="col-2 text-center" v-if="data?.counts">
              {{ $t("metadata.counts", { start: data?.counts?.start, numFound: data?.counts?.numFound }) }}
            </div>
            <div class="col-5 text-end">
              <nuxt-link :to="data?.next?.link"
                         v-if="data?.next">
                <span>{{ data?.next?.title }}</span>
                <i class="bi bi-chevron-right"></i>
              </nuxt-link>
            </div>
          </div>
        </div>
      </div>

      <div class="row sosu-detail-view__metadata">
        <div class="col-12">
          <MODSDocument :backend-url="sovietSurviorsURL" v-if="data?.xml" :xml="data?.xml" :id="mycoreId" projectDocumentUrlPrefix="/soviet-survivors/documents/" :filter-params="filterParams">

            <template #downloadLink>
              <MODSMetaKeyValue v-if="downloadLink">
                <template #key>
                  {{ $t("metadata.download") }}
                </template>
                <template #value>
                  <a :href="downloadLink" target="_blank">
                    <span class="bi bi-file-earmark-zip" />{{ $t("metadata.downloadText") }}
                  </a>
                </template>
              </MODSMetaKeyValue>
            </template>

            <template #media v-if="viewerLink">
              <client-only>
                <iframe sandbox="allow-modals allow-orientation-lock allow-pointer-lock allow-popups allow-popups-to-escape-sandbox allow-presentation allow-same-origin allow-scripts allow-top-navigation allow-top-navigation-by-user-activation" :src="viewerLink" class="viewer" frameborder="0" scrolling="no" />
              </client-only>

              <div class="sosu-detail-view__copyrights--images">
                {{ $t("sosu.metadata.copyright") }}
              </div>
            </template>
          </MODSDocument>
        </div>
      </div>
      <div class="row sosu-detail-view__copyrights">
        <div class="col">
          {{ $t("sosu.metadata.cite") }}
        </div>

        <div class="col-auto regest-licence text-end">
            <a href="https://creativecommons.org/licenses/by-sa/4.0/deed.de" title="CC BY-SA 4.0" class="no-external-mark">
              <nuxt-img src="/images/creative-commons.svg" alt="cc" />
              <nuxt-img src="/images/creative-commons-by.svg" alt="by" />
              <nuxt-img src="/images/creative-commons-sa.svg" alt="sa" />
            </a>
          </div>
      </div>
    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">


import {byName, findFirstElement, flattenElement, getAttribute, XMLApi} from "~/api/XMLApi";
import {getMyCoReId, getMyCoReIdNumber} from "~/api/MyCoRe";
import {
  buildSOSUSearchRequestURL, type Filters, modelToQuery, queryToModel,
  SoSuFilterParams, TranslationMode
} from "~/api/SearchHelper";

const {$sovietSurviorsURL, $sovietSurvivorsSolrURL} = useNuxtApp();
const route = useRoute();
const sovietSurviorsURL = $sovietSurviorsURL();
const sovietSurviorsSolrURL = $sovietSurvivorsSolrURL();

const docId = route.params.docId as string;
const OBJECT_PROJECT = "sovsurv";

const mycoreId = getMyCoReId(OBJECT_PROJECT, parseInt(docId));

const filterParams = SoSuFilterParams;

interface LinkInfo {
  title: string;
  link: string;
}

const {data, error} = await useAsyncData(route.fullPath, async () => {
  const q = route.query.q as string;
  const promises : Array<Promise<any>> = [ fetch(sovietSurviorsURL + `api/v2/objects/${mycoreId}`, {
    method: "GET",
  })
    .then((resp) => resp.text())
    .then((text) => XMLApi(text))];


  const model = {
    filters: { // enabled
      genres: [],
      languages: [],
      translationMode: TranslationMode.ALL,
    } as Filters,
    start: 0
  };
  queryToModel(route.query, model);

  if(q){
    promises.push(fetch(buildSOSUSearchRequestURL(sovietSurviorsSolrURL, q, model.filters, model.start == 0 ? model.start: model.start-1), {
      method: "GET",
      headers: {
        "Accept": "application/json",
      }
    }).then((resp) => resp.json()));
  }

  const [xml, searchResult] = await Promise.all(promises);

  console.log(["Data" ,xml, searchResult] )

  if(searchResult && q){
    const docs = searchResult.response.docs;

    const prev = model.start == 0 ? undefined : docs[0];
    const next = model.start == 0 ? docs[1] : docs[2];

    const query = modelToQuery(model);

    query.start =  (model.start-1)+"";
    const queryStrPrev = Object.keys(query).map((key) => `${key}=${query[key]}`).join("&");
    query.start =  (model.start +1)+"";
    const queryStrNext = Object.keys(query).map((key) => `${key}=${query[key]}`).join("&");


    return {
      xml,
      prev: prev ? {
        title: prev["mods.title.main"],
        link: `/soviet-survivors/documents/${getMyCoReIdNumber(prev["id"])}?${queryStrPrev}`
      } : undefined,
      next: next ? {
        title: next["mods.title.main"],
        link: `/soviet-survivors/documents/${getMyCoReIdNumber(next["id"])}?${queryStrNext}`
      } : undefined,
      counts: {
        start: model.start+1,
        numFound: searchResult.response.numFound
      }
    }
  } else {
    return {xml};
  }
});


const downloadLink = computed(() => {
  if(!data?.value?.xml){
    return undefined;
  }
  if (findFirstElement(data.value.xml, byName("derobject")) != null) {
    return `${sovietSurviorsURL}servlets/SovietSurvivorsExportServlet/?id=${mycoreId}`;
  } else {
    return undefined;
  }
})

const viewerLink = computed(() => {
  if(!data?.value?.xml){
    return undefined;
  }

  let firstDerivate = findFirstElement(data.value.xml, byName("derobject"));

  if (firstDerivate == null) {
    return undefined;
  }
  let href = getAttribute(firstDerivate, "xlink:href");
  if (href == null) {
    return undefined;
  }

  let maindocElem = findFirstElement(firstDerivate, byName("maindoc"));
  if (maindocElem == null) {
    return undefined;
  }

  let maindoc = flattenElement(maindocElem);
  if (maindoc == null) {
    return undefined;
  }

  return sovietSurviorsURL + "rsc/viewer/" + href.value + "/" + maindoc +"?frame=true&embedded=true";

});


</script>

<style>

.viewer {
  display: block;
  margin-top: 1rem;
  width: 100%;
  height: 500px;
}

</style>
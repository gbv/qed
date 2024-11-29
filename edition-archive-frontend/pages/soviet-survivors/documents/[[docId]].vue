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
              {{ $t("sosu.metadata.counts", { start: data?.counts?.start, numFound: data?.counts?.numFound }) }}
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
          <MODSDocument v-if="data?.xml" :xml="data?.xml" :id="mycoreId"/>
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


import {XMLApi} from "~/api/XMLApi";
import {getMyCoReId, getMyCoReIdNumber} from "~/api/MyCoRe";
import {buildSOSUSearchRequestURL, Filters, modelToQuery, queryToModel, TranslationMode} from "~/api/SearchHelper";

const {$sovietSurviorsURL, $sovietSurvivorsSolrURL} = useNuxtApp();
const route = useRoute();
const sovietSurviorsURL = $sovietSurviorsURL();
const sovietSurviorsSolrURL = $sovietSurvivorsSolrURL();

const docId = route.params.docId as string;
const OBJECT_PROJECT = "sovsurv";

const mycoreId = getMyCoReId(OBJECT_PROJECT, parseInt(docId));

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



</script>

<style>

</style>
<template>
  <SovietSurvivorsLayout>

    <template #content>
      <div class="row">
        <div class="col-12">
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
      <div class="row">
        <div class="col-12 mt-5">
          <MODSDocument v-if="data?.xml" :xml="data?.xml" :id="mycoreId"/>
        </div>
      </div>
    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">


import {XMLApi} from "~/api/XMLApi";
import {getMyCoReId, getMyCoReIdNumber} from "~/api/MyCoRe";
import {buildSOSUSearchRequestURL, TranslationMode} from "~/api/SearchHelper";

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

const {data, error} = await useAsyncData(mycoreId, async () => {
  const search = route.query.search as string;
  const start = route.query.start;

  const promises : Array<Promise<any>> = [ fetch(sovietSurviorsURL + `api/v2/objects/${mycoreId}`, {
    method: "GET",
  })
    .then((resp) => resp.text())
    .then((text) => XMLApi(text))];

  let startParam: number = 0;
  let searchStartParam: number = 0;

  if(search && start){
    startParam = parseInt(start as string);
    searchStartParam = startParam > 0 ? startParam - 1 : 0;

    promises.push(fetch(buildSOSUSearchRequestURL(sovietSurviorsSolrURL, search, {
      translationMode: TranslationMode.ALL,
      genres: [],
      languages: []
    }, searchStartParam), {
      method: "GET",
      headers: {
        "Accept": "application/json",
      }
    }).then((resp) => resp.json()));
  }

  const [xml, searchResult] = await Promise.all(promises);

  if(searchResult && search && start){
    const docs = searchResult.response.docs;

    const prev = startParam == 0 ? undefined : docs[0];
    const next = startParam == 0 ? docs[1] : docs[2];

    return {
      xml,
      prev: prev ? {
        title: prev["mods.title.main"],
        link: `/soviet-survivors/documents/${getMyCoReIdNumber(prev["id"])}?search=${search}&start=${startParam-1}`
      } : undefined,
      next: next ? {
        title: next["mods.title.main"],
        link: `/soviet-survivors/documents/${getMyCoReIdNumber(next["id"])}?search=${search}&start=${startParam+1}`
      } : undefined,
      counts: {
        start: startParam+1,
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
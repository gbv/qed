<template>
  <SovietSurvivorsLayout :headline="$t('search.label')">

    <template #content>
      <div class="card text-center mb-2">
        <div class="card-body">
          <h5 class="card-title">{{ $t("search.basic") }}</h5>
          <BasicSearchForm :searchString="model.searchString" v-on:search="changeURL"/>
        </div>
      </div>

      <SolrPaginator v-on:pageChanged="pageChangedCallback"
                     :count="model.count"
                     :start="model.start"
                     :numPerPage="20"/>

      <div v-if="model.result?.response?.docs?.length>0">
        <div class="search-result card mt-2 mb-2" v-for="(doc, index) in model.result.response.docs" :key="doc.id">


          <div class="card-body">
            <nuxt-link :to="`/soviet-survivors/documents/${getMyCoReIdNumber(doc['id'])}?search=${model.searchString}&start=${model.start + index}`" class="main-title">
              {{ doc["mods.title.main"] }}
            </nuxt-link>
            <div v-if="doc['mods.abstract']?.length>0">{{ trimString(doc["mods.abstract"][0]) }}</div>
          </div>

        </div>
      </div>

      <SolrPaginator v-on:pageChanged="pageChangedCallback"
                     :count="model.count"
                     :start="model.start"
                     :numPerPage="20"/>
    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">

import {LocationQuery} from "vue-router";
import {getMyCoReIdNumber} from "~/api/MyCoRe";
import {trimString} from "~/api/Utils";
import {buildSOSUSearchRequestURL} from "~/api/SearchHelper";

const {$sovietSurvivorsSolrURL} = useNuxtApp();
const route = useRoute();
const sovietSurviorsSolrURL = $sovietSurvivorsSolrURL();

const model = reactive({
  searchString: route.query.q as string || "*",
  result: null as any,
  count: 0,
  start: parseInt(route.query.start as string) || 0,
});

watch(() => route.query, async (newQueryString: LocationQuery, old: LocationQuery) => {
  model.searchString = newQueryString.q as string || "*";
  if (newQueryString.start) {
    model.start = parseInt(newQueryString.start as string);
  } else {
    model.start = 0;
  }
  await search();
});


const search = async () => {
  const url = buildSOSUSearchRequestURL(sovietSurviorsSolrURL, model.searchString, model.start);

  model.result = await fetch(url, {
    method: "GET",
    headers: {
      "Accept": "application/json",
    }
  }).then((resp) => resp.json());

  model.count = model.result.response.numFound;
}

const pageChangedCallback = (page: number) => {
  navigateTo({
    query: {
      ...route.query,
      start: (page - 1) * 20
    }
  })
}

const changeURL = (event: { searchString: string }) => {
  navigateTo({
    query: {
      q: event.searchString
    }
  })
}


await search();


</script>

<style scoped>


</style>
<template>
  <SovietSurvivorsLayout :headline="$t('search.label')">

    <template #content>
      <h3>{{$t("search.label")}}</h3>
      <!-- search form-->
      <div  class="row">
        <div class="col-12 col-lg-8">
          <BasicSearchForm :searchString="model.searchString" v-on:search="changeURL"/>
        </div>
      </div>

      <!-- error message-->
      <div class="row" v-if="model.error!=null">
        <div class="col-12 mt-4">
          <div class="alert alert-danger" role="alert">
            {{ $t("search.error", {error: model.error}) }}
          </div>
        </div>
      </div>

      <!-- result list and filter -->
      <div class="row results" v-else-if="model.result?.response?.docs?.length>0">

        <!-- results -->
        <div class="col-12 col-lg-8 order-2 order-lg-1 results__list">

          <!-- results: headline and sorting-->
          <div class="row g-0 result-options">
            <div class="col hit-count ms-3">
              <h2>{{ $t('search.hitCount', { count: model.count }) }}</h2>
            </div>
          </div>

          <div class="result_list">
            <div id="hit_list">

              <div
                class="hit_item"
                v-for="(doc, index) in model.result.response.docs" :key="doc.id">


                <div class="row hit_item_head">
                  <div class="col-12">
                    <div class="hit_counter">{{index + 1 + model.start}}</div>
                  </div>
                </div>

                <div class="row hit_item_body">
                  <div class="col-12">
                    <div class="hit_download_box">
                      <a title="Sammelwerk"
                        href=""
                        class="hit_option hit_download">
                        <div class="hit_icon">
                        </div>
                      </a>
                    </div>

                    <h3 class="hit_title">
                      <nuxt-link
                        :to="`/soviet-survivors/documents/${getMyCoReIdNumber(doc['id'])}?search=${model.searchString}&start=${model.start + index}`"
                        class="main-title">
                        {{ doc["mods.title.main"] }}
                      </nuxt-link>
                    </h3>
                    <div class="hit_number">
                    </div>
                    <div class="hit_abstract">
                      <div v-if="doc['mods.abstract']?.length>0">
                        {{ trimString(doc["mods.abstract"][0]) }}
                      </div>
                    </div>
                  </div>
                </div>

              </div>

            </div>
          </div>

          <h2 v-if="model.count===0" class="text-center mt-5">{{ $t('search.noHits') }}</h2>

          <SolrPaginator v-on:pageChanged="pageChangedCallback"
                         :count="model.count"
                         :start="model.start"
                         :numPerPage="20"/>
        </div>

        <!-- facets -->
        <div class="col-12 col-lg-4 order-1 order-lg-2 text-end text-lg-start results__facets">
        </div>
      </div>

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
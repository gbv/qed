<template>
  <div>
    <div class="row">
      <div class="col">
        <h2 class="mb-5">Gallia Pontificia Online</h2>
      </div>
    </div>
    <div class="row">
      <div class="col-3">
        <gallia-pontifica-online-menu/>
      </div>
      <div class="col-9">
        <div class="row">
          <div class="col-12">
            <!-- Search Form -->
            <tabs :tabs="tabData" card-class="text-center" current="basic"
                  v-on:tabChanged="tabChanged">
              <template v-slot:basic>
                <BasicSearch v-on:search="basicSearchCallback" :searchString="model.searchString"/>
              </template>
              <template v-slot:extended>
                extended
              </template>
            </tabs>
          </div>
        </div>
        <div class="row" v-if="model.searchResult">
          <div class="col-12 mt-3">
            <!-- Search Results -->

            <solr-paginator v-on:pageChanged="pageChangedCallback"
                            :count="model.count"
                            :start="model.start"
                            :numPerPage="20"/>

            <article class="search-result card mt-2 mb-2" v-for="result in model.searchResult.response.docs">
              <section class="card-body">
                <nuxt-link :href="`/gallia_pontifica_online/regest/${result.idno}`"
                           :title="$t('go_to_regest', {regest:result.idno})">
                  <RegestId :lost="result.lost" :certainly="result.certainly" :fake="result.fake" :idno="result.idno"/>
                </nuxt-link>
              </section>
            </article>

            <h2 v-if="model.count===0" class="text-center mt-5">{{ $t('search_no_hits') }}</h2>

            <solr-paginator v-on:pageChanged="pageChangedCallback"
                            :count="model.count"
                            :start="model.start"
                            :numPerPage="20"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {useI18n} from 'vue-i18n';
import {createError} from "h3";
import BasicSearch from "../../../components/BasicSearch";

const i18n = useI18n();
const BASIC_SEARCH_TYPE = "basic";
const EXTENDED_SEARCH_TYPE = "extended";

const tabData = ref([
  {id: BASIC_SEARCH_TYPE, title: i18n.t('search_basic')},
  {id: EXTENDED_SEARCH_TYPE, title: i18n.t('search_extended')}
]);
const {$solrURL, $backendURL} = useNuxtApp();
const solrURL = $solrURL();
const model = reactive(
    {
      searchResult: undefined,
      count: 0,
      start: 0,
      searchString: ""
    });
const route = useRoute();

const {searchType} = route.params;

const escapeSpecialChars = (s) => s
    .replace(/([\+\-!\(\)\{\}\[\]\^"~\*\?:\\\/])/g, function (match) {
      return '\\' + match;
    })
    .replace(/&&/g, '\\&\\&')
    .replace(/\|\|/g, '\\|\\|');

async function triggerSearch(query) {
  switch (searchType) {
    case "":
      break;
    case BASIC_SEARCH_TYPE:
      if (query.searchString) {
        console.log("Assign searchstring " + query.searchString);
        model.searchString = query.searchString;
        let url = `${$solrURL()}main/select/?q=allMeta:${query.searchString === "" ? "*" : escapeSpecialChars(query.searchString)} AND objectKind:mycoreobject AND objectProject:gpo&wt=json`;

        if (query.start) {
          url += "&start=" + query.start;
        }
        const request = await fetch(url)
        const searchResult = await request.json();
        model.searchResult = searchResult;
        model.count = searchResult.response.numFound;
        model.start = searchResult.response.start;

      }
      break;
    default:
      throwError(
          createError({
            statusCode: 404,
            statusMessage: 'Not Found',
          })
      );
  }
}

await triggerSearch(route.query);

const tabChanged = (obj) => {
  const {old, _new} = obj;
}

const basicSearchCallback = async (searchParameters) => {
  navigateTo({
    path: "./basic",
    query: {
      searchString: searchParameters.searchString
    }
  })
}
const pageChangedCallback = async (newPage) => {
  navigateTo({
    path: "./basic",
    query: {
      searchString: route.query.searchString,
      start: (newPage - 1) * 20
    }
  });
}

watch(() => route.query, async (_new, old) => {
  console.log({old});
  console.log({_new});
  triggerSearch(_new);
});

</script>


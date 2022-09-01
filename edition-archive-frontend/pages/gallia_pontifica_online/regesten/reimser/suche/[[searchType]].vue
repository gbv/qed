<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
      <GalliaPontificaOnlineReimser>
        <template #regesten>
          <div class="row" v-if="model.currentTab === BASIC_SEARCH_TYPE || model.currentTab === EXTENDED_SEARCH_TYPE">
            <div class="col-12">
              <!-- Search Form -->
              <TabsCard :tabs="tabData" card-class="text-center" :current="model.currentTab"
                    v-on:tabChanged="tabChanged">
                <template v-slot:einfach>
                  <BasicSearchForm v-on:search="basicSearchCallback" :searchString="model.searchString"/>
                </template>
                <template v-slot:erweitert>
                  <ExtendedSearchForm v-on:search="extendedSearchCallback"
                                  :allMeta="model.extendedSearch.allMeta"
                                  :person="model.extendedSearch.person"
                                  :place="model.extendedSearch.place"
                                  :initium="model.extendedSearch.initium"
                                  :issuer="model.extendedSearch.issuer"
                                  :recipient="model.extendedSearch.recipient"
                                  :lost="model.extendedSearch.lost"
                                  :fake="model.extendedSearch.fake"
                                  :certainly="model.extendedSearch.certainly"
                                  :dateRangeRange="model.extendedSearch.dateRangeRange"
                                  :dateRangeFrom="model.extendedSearch.dateRangeFrom"
                                  :dateRangeTo="model.extendedSearch.dateRangeTo"
                                  :dateText="model.extendedSearch.dateText"
                  />
                </template>
              </TabsCard>
            </div>
          </div>
          <div class="row" v-if="model.searchResult">
            <div class="col-12 mt-3">
              <!-- Search Results -->

              <SolrPaginator v-on:pageChanged="pageChangedCallback"
                              :count="model.count"
                              :start="model.start"
                              :numPerPage="20"/>

              <article class="search-result card mt-2 mb-2" v-for="result in model.searchResult.response.docs">
                <section class="card-body">
                  <div><span class="issuer">{{ result.pontifikatAEP?.join(",") }} - </span></div>
                  <nuxt-link :href="`/gallia_pontifica_online/regest/${result.idno}`"
                             :title="$t('go_to_regest', {regest:result.idno})">
                    <GalliaPontificaOnlineRegestId :lost="result.lost" :certainly="result.certainly" :fake="result.fake" :idno="result.idno"/>
                    , {{ [result['issued.text']?.join(", "), result.issuedPlace?.join(", ")].join(", ") }}
                  </nuxt-link>
                  <p v-if="'regest.json' in result">
                    {{ trimString(flattenElement(findFirstElement(result['regest.json'], byName("cei:abstract")))) }}</p>
                </section>
              </article>

              <h2 v-if="model.count===0" class="text-center mt-5">{{ $t('search_no_hits') }}</h2>

              <SolrPaginator v-on:pageChanged="pageChangedCallback"
                              :count="model.count"
                              :start="model.start"
                              :numPerPage="20"/>
            </div>
          </div>
        </template>
      </GalliaPontificaOnlineReimser>
    </template>
  </GalliaPontificaOnlineLayout>
</template>

<script setup lang="ts">
  import {useI18n} from 'vue-i18n';
  import {createError} from "h3";
  import {XMLApi, findFirstElement, flattenElement, byName} from "~/api/XMLApi";
  import SolrPaginator from "~/components/SolrPaginator.vue";

  const i18n = useI18n();
  const BASIC_SEARCH_TYPE = "einfach";
  const EXTENDED_SEARCH_TYPE = "erweitert";

  const tabData = ref([
    {id: BASIC_SEARCH_TYPE, title: i18n.t('search_basic')},
    {id: EXTENDED_SEARCH_TYPE, title: i18n.t('search_extended')}
  ]);
  const {$solrURL, $backendURL} = useNuxtApp();
  const solrURL = $solrURL();
  const route = useRoute();

  const model = reactive(
      {
        searchResult: undefined,
        count: 0,
        start: 0,
        personObj: null,
        searchString: null,
        extendedSearch: {
          allMeta: null,
          person: null,
          place: null,
          initium: null,
          issuer: null,
          recipient: null,
          lost: null,
          fake: null,
          certainly: null,
          dateRangeFrom: null,
          dateRangeTo: null,
          dateRangeRange: false,
          dateText: null
        },
        currentTab: route.params.searchType || BASIC_SEARCH_TYPE
      });

  const escapeSpecialChars = (s) => s
      .replace(/([\+\-!\(\)\{\}\[\]\^"~\*\?:\\\/])/g, function (match) {
        return '\\' + match;
      })
      .replace(/&&/g, '\\&\\&')
      .replace(/\|\|/g, '\\|\\|');

  async function executeSearch(url, query) {
    if (query.start) {
      url += "&start=" + query.start;
    }
    const request = await fetch(url)
    const searchResult = await request.json();
    model.searchResult = searchResult;
    for (const doc of model.searchResult.response.docs) {
      const xmlCode = doc["regest.xml"];
      doc["regest.json"] = await XMLApi(xmlCode);
    }
    model.count = searchResult.response.numFound;
    model.start = searchResult.response.start;
  }

  async function triggerSearch(query) {
    switch (route.params.searchType) {
      case "":
        break;
      case BASIC_SEARCH_TYPE:
        if (query.searchString) {
          model.searchString = query.searchString;
          model.currentTab = BASIC_SEARCH_TYPE;
          let url = `${$solrURL()}main/select/?q=allMeta:${query.searchString === "" ? "*" : escapeSpecialChars(query.searchString)} AND objectType:regest AND objectProject:gpo&wt=json`;

          await executeSearch(url, query);
        }
        break;
      case EXTENDED_SEARCH_TYPE:
        model.currentTab = EXTENDED_SEARCH_TYPE;
        for (const key in model.extendedSearch) {
          if (key in query) {
            model.extendedSearch[key] = query[key];
          } else {
            model.extendedSearch[key] = null;
          }
        }

        const q = ["objectType:regest", "objectProject:gpo"];

        if (model.extendedSearch.allMeta != null) {
          let allMeta = model.extendedSearch.allMeta;
          if (allMeta == "") {
            allMeta = "*";
          }
          q.push(`allMeta:${escapeSpecialChars(allMeta)}`);
        }

        if (model.extendedSearch.person != null && model.extendedSearch.person != "") {
          const escapedPerson = escapeSpecialChars(model.extendedSearch.person);
          q.push(`(recipient:${escapedPerson} OR issuer:${escapedPerson})`);
        }

        if (model.extendedSearch.place != null && model.extendedSearch.place != "") {
          const escapedPlace = escapeSpecialChars(model.extendedSearch.place);
          q.push(`issuedPlace:${escapedPlace}`);
        }

        if (model.extendedSearch.initium != null && model.extendedSearch.initium != "") {
          const escapedInitium = escapeSpecialChars(model.extendedSearch.initium);
          q.push(`initium:${escapedInitium}`);
        }

        if (model.extendedSearch.recipient != null && model.extendedSearch.recipient != "") {
          const escapedRecipient = escapeSpecialChars(model.extendedSearch.recipient);
          q.push(`recipient:${escapedRecipient}`);
        }

        if (!model.extendedSearch.dateRangeRange) {
          if (model.extendedSearch.dateRangeFrom != null && model.extendedSearch.dateRangeFrom != "") {
            q.push(`issued.range:${model.extendedSearch.dateRangeFrom}`);
          }
        } else {
          if (model.extendedSearch.dateRangeFrom != null && model.extendedSearch.dateRangeFrom != "" &&
              model.extendedSearch.dateRangeTo != null && model.extendedSearch.dateRangeTo != "") {
            q.push(`issued.range:[${model.extendedSearch.dateRangeFrom} TO ${model.extendedSearch.dateRangeTo}]`)
          } else if (model.extendedSearch.dateRangeTo != null && model.extendedSearch.dateRangeTo != "") {
            q.push(`issued.range:[* TO ${model.extendedSearch.dateRangeTo}]`)
          } else if (model.extendedSearch.dateRangeFrom != null && model.extendedSearch.dateRangeFrom) {
            q.push(`issued.range:[${model.extendedSearch.dateRangeFrom} TO *]`)
          }
        }

        if (model.extendedSearch.dateText != null && model.extendedSearch.dateText != "") {
          const dateTextEscapted = escapeSpecialChars(model.extendedSearch.dateText);
          q.push(`issued.text:${dateTextEscapted}`);
        }

        let url = `${$solrURL()}main/select/?q=${q.join(" AND ")}&wt=json`
        await executeSearch(url, query);
        break;
      case "person":
        if (query.personObj) {
          model.personObj = query.personObj;
          let url = `${$solrURL()}main/select/?q=${model.personObj} AND objectType:regest AND objectProject:gpo&wt=json`;
          await executeSearch(url, query);
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
      path: "./" + BASIC_SEARCH_TYPE,
      query: {
        searchString: searchParameters.searchString
      }
    })
  }

  const extendedSearchCallback = async (searchParameters) => {
    navigateTo({
      path: "./" + EXTENDED_SEARCH_TYPE,
      query: {
        ...searchParameters
      }
    })
  }

  const pageChangedCallback = async (newPage) => {
    navigateTo({
      path: "./" + model.currentTab,
      query: {
        ...route.query,
        start: (newPage - 1) * 20
      }
    });
  }

  const trimString = (str) => {
    const size = 240;

    if (str.length > size) {
      return str.substring(0, str.indexOf(' ', size)) + "…";
    } else {
      return str;
    }
  }
  watch(() => route.query, async (newQueryString, old) => {
    triggerSearch(newQueryString);
  });

</script>


<style scoped>
  .issuer {
    font-weight: bold;
  }
</style>
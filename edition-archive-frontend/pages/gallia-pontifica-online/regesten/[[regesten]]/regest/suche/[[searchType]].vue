<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
      <div class="row" v-if="model.currentTab === BASIC_SEARCH_TYPE || model.currentTab === EXTENDED_SEARCH_TYPE">
        <div class="col-12" v-if="tabData">
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
                                  :jaffe2="model.extendedSearch.jaffe2"
                                  :jaffe3="model.extendedSearch.jaffe3"
                  />
                </template>
              </TabsCard>
            </div>
          </div>
          <div class="row hit-count">
            <h2>{{ $t('search.hitCount', { count: model.count }) }}</h2>
          </div>
          <div class="row sort">
            <div class="col-6">
              <select class="form-select icon-hack" v-model="model.sortOrder" v-on:change="sortChanged">
                <option value="asc">{{$t("search.sort.asc")}}</option>
                <option value="desc">{{ $t("search.sort.desc") }}</option>
              </select>
            </div>
            <div class="col-6">
              <select class="form-select" v-model="model.sort" v-on:change="sortChanged">
                <option value="relevance">{{ $t("search.sort.relevance") }}</option>
                <option value="idno">{{ $t("search.sort.idno") }}</option>
              </select>
            </div>
          </div>
      <div class="row" v-if="model.error!=null">
        <div class="col-12 mt-4">
          <div class="alert alert-danger" role="alert">
            {{ $t("search.error", {error: model.error}) }}
          </div>
        </div>
      </div>
      <div class="row results" v-else-if="model.searchResult">
        <div class="col-12">
          <!-- Search Results -->

          <SolrPaginator v-on:pageChanged="pageChangedCallback"
                         :count="model.count"
                         :start="model.start"
                         :numPerPage="20"/>

          <article class="search-result card mt-2 mb-2" v-for="result in model.searchResult.response.docs">
            <section class="card-body">
              <div><span class="issuer" v-if="'regest.json' in result">
                    {{
                  flattenElement(findFirstElement(result['regest.json'], and(byName("cei:p"), byAttr('type', 'PontifikatAEP'))))
                }} – {{
                  flattenElement(findFirstElement(result['regest.json'], and(byName("cei:p"), byAttr('type', 'PontifikatPP'))))
                }}</span></div>
              <nuxt-link :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/${result.idno}`"
                         :title="$t('search.goToRegest', {regest:result.idno})">
                Nr.
                <GalliaPontificaOnlineRegestId :idno="result.idno" :certainly="result.certainly" :fake="result.fake"
                                               :lost="result.lost"/>. {{
                  [result.issuedPlace?.join(", "), result['issued.text']?.join(", ")].filter(p => p !== null && p !== undefined).join(", ")
                }}
              </nuxt-link>
              <p v-if="'regest.json' in result">
                {{ trimString(flattenElement(findFirstElement(result['regest.json'], byName("cei:abstract")))) }}
              </p>
              <i v-if="'regest.json' in result" class="fst-italic">
                {{
                  trimString(findElement(result['regest.json'], byName('cei:incipit')).map(flattenElement).join("; "))
                }}
              </i>

            </section>
              </article>

              <h2 v-if="model.count===0" class="text-center mt-5">{{ $t('search.noHits') }}</h2>

              <SolrPaginator v-on:pageChanged="pageChangedCallback"
                             :count="model.count"
                             :start="model.start"
                             :numPerPage="20"/>
            </div>
          </div>
    </template>
    <template #menu>
      <GalliaPontificaOnlineMenu/>

      <div v-if="model.facet.ueberlieferungsform.length>0" class="facet">
        <h4 class="facet-title text-center">{{ $t('search.facet.ueberlieferungsform') }}</h4>
        <ul class="list-group">

          <li v-if=" model.facet?.lost?.find((s)=> s.name==='true')?.count" :class="model.facet.lostValues===true ? 'active':''" class="list-group-item facet-item d-flex justify-content-between align-items-center clickable"
          v-on:click="facetClicked('lost', true)">
           {{ $t('search.facet.deperditum') }}
            <span class="badge bg-primary rounded-pill">{{ model.facet?.lost?.find((s)=> s.name==="true")?.count || 0 }}</span>
          </li>

          <li v-if="model.facet?.fake?.find((s)=> s.name==='true')?.count" :class="model.facet.fakeValues===true ? 'active':''" class="list-group-item facet-item d-flex justify-content-between align-items-center clickable"
          v-on:click="facetClicked('fake', true)">
           {{ $t('search.facet.spurium') }}
            <span class="badge bg-primary rounded-pill">{{ model.facet?.fake?.find((s)=> s.name==="true")?.count || 0 }}</span>
          </li>


          <template v-if="model.facet.ueberlieferungsform.length>0">
            <li
              v-for="ueberlieferungsform in model.facet.ueberlieferungsformExpand ? model.facet.ueberlieferungsform: model.facet.ueberlieferungsform.slice(0,10)"
              :class="model.facet.ueberlieferungsformEnabledValues.indexOf(ueberlieferungsform.name)>-1?'active':''"
              class="list-group-item facet-item d-flex justify-content-between align-items-center clickable"
              v-on:click="facetClicked('ueberlieferungsform', ueberlieferungsform.name)">
              {{ ueberlieferungsform.name }}
              <span class="badge bg-primary rounded-pill">{{ ueberlieferungsform.count }}</span>
            </li>
            <a v-if="model.facet.ueberlieferungsformExpand===false && model.facet.ueberlieferungsform.length>10"
               href="#more" v-on:click.prevent="model.facet.ueberlieferungsformExpand=true">{{ $t('search.facet.showMore') }}</a>
            <a v-if="model.facet.ueberlieferungsformExpand===true && model.facet.ueberlieferungsform.length>10"
               href="#less" v-on:click.prevent="model.facet.ueberlieferungsformExpand=false">{{ $t('search.facet.showLess') }}</a>
          </template>
        </ul>
      </div>

      <div v-if="model.facet.issuer.length>0" class="facet">
        <h4 class="facet-title text-center">{{ $t('search.facet.issuer') }}</h4>
        <ul class="list-group">
          <li v-for="issuer in model.facet.issuerExpand ? model.facet.issuer: model.facet.issuer.slice(0,10)"
              :class="model.facet.issuerEnabledValues.indexOf(issuer.name)>-1?'active':''"
              class="list-group-item facet-item d-flex justify-content-between align-items-center clickable"
              v-on:click="facetClicked('issuer', issuer.name)">
            {{ issuer.name }}
            <span class="badge bg-primary rounded-pill">{{ issuer.count }}</span>
            <!-- </nuxt-link> -->
          </li>
        </ul>
        <a v-if="model.facet.issuerExpand===false && model.facet.issuer.length>10"
           href="#more" v-on:click.prevent="model.facet.issuerExpand=true">{{ $t('search.facet.showMore') }}</a>
        <a v-if="model.facet.issuerExpand===true && model.facet.issuer.length>10"
           href="#less" v-on:click.prevent="model.facet.issuerExpand=false">{{ $t('search.facet.showLess') }}</a>
      </div>

      <div v-if="model.facet.recipient.length>0" class="facet">
        <h4 class="facet-title text-center">{{ $t('search.facet.recipient') }}</h4>
        <ul class="list-group">
          <li
            v-for="recipient in model.facet.recipientExpand ? model.facet.recipient: model.facet.recipient.slice(0,10)"
            :class="model.facet.recipientEnabledValues.indexOf(recipient.name)>-1?'active':''"
            class="list-group-item facet-item d-flex justify-content-between align-items-center clickable"
            v-on:click="facetClicked('recipient', recipient.name)">
            {{ recipient.name }}
            <span class="badge bg-primary rounded-pill">{{ recipient.count }}</span>
          </li>
        </ul>
        <a v-if="model.facet.recipientExpand===false && model.facet.recipient.length>10"
           href="#more" v-on:click.prevent="model.facet.recipientExpand=true">{{ $t('search.facet.showMore') }}</a>
        <a v-if="model.facet.recipientExpand===true && model.facet.recipient.length>10"
           href="#less" v-on:click.prevent="model.facet.recipientExpand=false">{{ $t('search.facet.showLess') }}</a>
      </div>
    </template>
  </GalliaPontificaOnlineLayout>
</template>

<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import {XMLApi} from "~/api/XMLApi";
import {byName, findFirstElement, flattenElement, byAttr, and, findElement} from "@mycore-org/xml-json-api"
import SolrPaginator from "~/components/SolrPaginator.vue";
import {LocationQuery, LocationQueryValue} from "vue-router";
import {partialEscapeSpecialChars, trimString} from "~/api/Utils";

const i18n = useI18n();
const route = useRoute();

const BASIC_SEARCH_TYPE = "einfach";
const EXTENDED_SEARCH_TYPE = "erweitert";

const tabData = ref([
  {id: BASIC_SEARCH_TYPE, title: i18n.t('search.basic')},
  {id: EXTENDED_SEARCH_TYPE, title: i18n.t('search.extended')}
]);
const {$solrURL, $backendURL} = useNuxtApp();
const solrURL = $solrURL();

interface Model {
  searchResult: any,
  count: number,
  start: number,
  personObj: string | null,
  organisationObj: string | null,
  ortObj: string | null,
  quellenKey: string | null,
  handschriftenKey: string | null,
  dekretaleKey: string | null,
  searchString: string | null,
  extendedSearch: ExtendedSearchModel,
  facet: FacetModel,
  currentTab: string,
  sort: "relevance" | "idno",
  sortOrder: "asc" | "desc",
  error: string | null,
}

interface ExtendedSearchModel {
  allMeta: string | null,
  person: string | null,
  place: string | null,
  initium: string | null,
  recipient: string | null,
  lost: boolean | null,
  fake: boolean | null,
  certainly: boolean | null,
  dateRangeFrom: string | null,
  dateRangeTo: string | null,
  dateRangeRange: boolean | null,
  dateText: string | null,
  issuer: string | null,
  jaffe2: string | null,
  jaffe3: string | null,
}

interface FacetModel {
  recipient: FacetEntry[],
  recipientExpand: boolean,
  recipientEnabledValues: string[],
  issuer: FacetEntry[],
  issuerExpand: boolean,
  issuerEnabledValues: string[],
  ueberlieferungsform: FacetEntry[],
  ueberlieferungsformExpand: boolean,
  ueberlieferungsformEnabledValues: string[],
  lost: FacetEntry[],

  lostValues: boolean,
  fake: FacetEntry[],

  fakeValues: boolean,

  [key: string]: FacetEntry[]| boolean | string[]
}

interface FacetEntry {
  name: string,
  count: number
}

const model: Model = reactive(
  {
    searchResult: undefined,
    count: 0,
    start: 0,
    personObj: null,
    organisationObj: null,
    ortObj: null,
    quellenKey: null,
    handschriftenKey: null,
    dekretaleKey: null,
    searchString: null,
    facet: {
      recipient: [],
      recipientExpand: false,
      recipientEnabledValues: [],
      issuer: [],
      issuerExpand: false,
      issuerEnabledValues: [],
      ueberlieferungsform: [],
      ueberlieferungsformExpand: false,
      ueberlieferungsformEnabledValues: [],
      lost: [],
      fake: [],
      lostValues: false,
      fakeValues: false,
    },
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
      dateText: null,
      jaffe2: null,
      jaffe3: null,
    },
    currentTab: queryToString(route?.params.searchType) || BASIC_SEARCH_TYPE,
    sort: "relevance",
    sortOrder: "desc",
    error: null,
  });


async function executeSearch(url: string, query: LocationQuery) {
  try {
    if (query.start) {
      url += "&start=" + query.start;
    }

    url += "&q.op=AND&facet.field=recipient.facet&facet.field=issuer.facet&facet.field=ueberlieferungsform.facet&facet.field=lost&facet.field=fake&facet=on";

    if (model.facet.recipientEnabledValues.length > 0) {
      url += "&fq=recipient.facet:(" + model.facet.recipientEnabledValues.map(partialEscapeSpecialChars).map((q) => `"${q}"`).join(" AND ") + ")";
    }
    if (model.facet.issuerEnabledValues.length > 0) {
      url += "&fq=issuer.facet:(" + model.facet.issuerEnabledValues.map(partialEscapeSpecialChars).map((q) => `"${q}"`).join(" AND ") + ")";
    }
    if (model.facet.ueberlieferungsformEnabledValues.length > 0) {
      url += "&fq=ueberlieferungsform.facet:(" + model.facet.ueberlieferungsformEnabledValues.map(partialEscapeSpecialChars).map((q) => `"${q}"`).join(" AND ") + ")";
    }

    if (model.facet.lostValues) {
      url += "&fq=lost:true";
    }

    if (model.facet.fakeValues) {
      url += "&fq=fake:true";
    }

    if (model.sort === "relevance") {
      url += "&sort=score " + model.sortOrder + ",idno " + model.sortOrder;
    } else {
      url += "&sort=idno " + model.sortOrder;
    }

    const request = await fetch(url)

    if (request.status != 200) {
      model.error = request.statusText;
      return;
    }

    const searchResult = await request.json();
    model.searchResult = searchResult;
    const proms = [];
    for (const doc of model.searchResult.response.docs) {
      const xmlCode = doc["regest.xml"];
      proms.push(XMLApi(xmlCode));
    }

    const values = await Promise.all(proms);
    for (let i = 0; i < values.length; i++) {
      model.searchResult.response.docs[i]["regest.json"] = values[i];
    }

    for (const facetName in model.searchResult.facet_counts.facet_fields) {
      const facetValues = model.searchResult.facet_counts.facet_fields[facetName];
      const facetEntries: FacetEntry[] = [];
      for (let i = 0; i < facetValues.length; i += 2) {
        if (facetValues[i + 1] !== 0) {
          facetEntries.push({name: facetValues[i], count: facetValues[i + 1]});
        }
      }
      model.facet[facetName.split(".")[0]] = facetEntries;
    }
    model.error = null;
    model.count = searchResult.response.numFound;
    model.start = searchResult.response.start;
  } catch (e) {
    model.error = (e as any).toString();
  }
}

function applyQueryFacet(query: LocationQuery) {
  if (typeof query.issuerFacet === "string") {
    model.facet.issuerEnabledValues = [query.issuerFacet as string];
  } else if (Array.isArray(query.issuerFacet)) {
    model.facet.issuerEnabledValues = query.issuerFacet as string[];
  }

  if (typeof query.recipientFacet === "string") {
    model.facet.recipientEnabledValues = [query.recipientFacet as string];
  } else if (Array.isArray(query.recipientFacet)) {
    model.facet.recipientEnabledValues = query.recipientFacet as string[];
  }

  if (typeof query.ueberlieferungsformFacet === "string") {
    model.facet.ueberlieferungsformEnabledValues = [query.ueberlieferungsformFacet as string];
  } else if (Array.isArray(query.ueberlieferungsformFacet)) {
    model.facet.ueberlieferungsformEnabledValues = query.ueberlieferungsformFacet as string[];
  }

  if(typeof query.lostFacet === "string") {
    model.facet.lostValues = query.lostFacet === "true";
  }

  if(typeof query.fakeFacet === "string") {
    model.facet.fakeValues = query.fakeFacet === "true";
  }


}

function applySort(query: LocationQuery) {
  if (query.sort == "relevance" || query.sort == "idno") {
    model.sort =  query.sort;
  }
  if (query.sortOrder == "asc" || query.sortOrder == "desc") {
    model.sortOrder =  query.sortOrder;
  }
}

async function triggerSearch(query: LocationQuery) {
  switch (route.params.searchType) {
    case "":
      break;
    case BASIC_SEARCH_TYPE:
      if (query.searchString) {
        model.searchString = queryToString(query.searchString) || "";
        model.currentTab = BASIC_SEARCH_TYPE;

        let url: string;
        if (model.searchString === "") {
          url = `${$solrURL()}main/select/?q=allMeta:* AND objectType:regest AND objectProject:gpo&wt=json`;
        } else {
          let escapedAllMeta = partialEscapeSpecialChars(model.searchString);
          url = `${$solrURL()}main/select/?q=(allMeta:${escapedAllMeta} OR allMeta.de:${escapedAllMeta} OR allMeta.en:${escapedAllMeta} OR allMeta.fr:${escapedAllMeta}) AND objectType:regest AND objectProject:gpo&wt=json`;
        }

        applyQueryFacet(query);
        applySort(query);
        await executeSearch(url, query);
      }
      break;
    case EXTENDED_SEARCH_TYPE:
      model.currentTab = EXTENDED_SEARCH_TYPE;
      for (const key in model.extendedSearch) {
        if (key in query) {
          (model.extendedSearch as any)[key] = query[key];
        } else {
          (model.extendedSearch as any)[key] = null;
          }
        }

      const q = ["objectType:regest", "objectProject:gpo"];

      if (model.extendedSearch.allMeta != null) {
        let allMeta = model.extendedSearch.allMeta;
        if (allMeta == "") {
          allMeta = "*";
        }
        let escapedAllMeta = partialEscapeSpecialChars(allMeta);
        q.push(`(allMeta:${escapedAllMeta} OR allMeta.de:${escapedAllMeta} OR allMeta.en:${escapedAllMeta} OR allMeta.fr:${escapedAllMeta})`);
      }

      if (model.extendedSearch.person != null && model.extendedSearch.person != "") {
        const escapedPerson = partialEscapeSpecialChars(model.extendedSearch.person);
        q.push(`(person:${escapedPerson} OR person.de:${escapedPerson} OR person.en:${escapedPerson} OR person.fr:${escapedPerson})`);
      }

      if (model.extendedSearch.place != null && model.extendedSearch.place != "") {
        const escapedPlace = partialEscapeSpecialChars(model.extendedSearch.place);
        q.push(`(issuedPlace:${escapedPlace} OR issuedPlace.de:${escapedPlace} OR issuedPlace.en:${escapedPlace} OR issuedPlace.fr:${escapedPlace}`
        + `OR place:${escapedPlace} OR place.de:${escapedPlace} OR place.en:${escapedPlace} OR place.fr:${escapedPlace}`
        + `OR organization:${escapedPlace} OR organization.de:${escapedPlace} OR organization.en:${escapedPlace} OR organization.fr:${escapedPlace}`
        + `)`);

      }

      if (model.extendedSearch.initium != null && model.extendedSearch.initium != "") {
        const escapedInitium = partialEscapeSpecialChars(model.extendedSearch.initium);
        q.push(`initium:${escapedInitium}`);
      }

      if (model.extendedSearch.recipient != null && model.extendedSearch.recipient != "") {
        const escapedRecipient = partialEscapeSpecialChars(model.extendedSearch.recipient);
        q.push(`(recipient:${escapedRecipient} OR recipient.de:${escapedRecipient} OR recipient.en:${escapedRecipient} OR recipient.fr:${escapedRecipient})`);
      }

      if (model.extendedSearch.issuer != null && model.extendedSearch.issuer != "") {
        const escapedIssuer = partialEscapeSpecialChars(model.extendedSearch.issuer);
        q.push(`(issuer:${escapedIssuer} OR issuer.de:${escapedIssuer} OR issuer.en:${escapedIssuer} OR issuer.fr:${escapedIssuer})`);
      }

      if(model.extendedSearch.jaffe2 != null && model.extendedSearch.jaffe2 != "") {
        const escapedJaffe2 = partialEscapeSpecialChars(model.extendedSearch.jaffe2);
        q.push(`jaffe2:*${escapedJaffe2}*`);
      }

      if(model.extendedSearch.jaffe3 != null && model.extendedSearch.jaffe3 != "") {
        const escapedJaffe3 = partialEscapeSpecialChars(model.extendedSearch.jaffe3);
        q.push(`jaffe3:*${escapedJaffe3}*`);
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
        const dateTextEscapted = partialEscapeSpecialChars(model.extendedSearch.dateText);
        q.push(`issued.text:${dateTextEscapted}`);
      }
      applyQueryFacet(query);
      applySort(query);

      let url = `${$solrURL()}main/select/?q=${q.join(" AND ")}&wt=json`
      await executeSearch(url, query);
      break;
    case "person":
      if (query.personObj) {
        model.personObj = queryToString(query.personObj);
        applyQueryFacet(query);
        applySort(query);

        let url = `${$solrURL()}main/select/?q=person.obj:${model.personObj} AND objectType:regest AND objectProject:gpo&wt=json`;
        await executeSearch(url, query);
      }
      break;
    case "organisation":
      if(query.organisationObj){
        model.organisationObj = queryToString(query.organisationObj);
        applyQueryFacet(query);
        applySort(query);

        let url = `${$solrURL()}main/select/?q=organization.obj:${model.organisationObj} AND objectType:regest AND objectProject:gpo&wt=json`;
        await executeSearch(url, query);
      }
      break;
    case "ort":
      if (query.ortObj) {
        model.ortObj = queryToString(query.ortObj);
        applyQueryFacet(query);
        applySort(query);

        let url = `${$solrURL()}main/select/?q=place.obj:${model.ortObj} AND objectType:regest AND objectProject:gpo&wt=json`;
        await executeSearch(url, query);
      }
      break;
    case "quellen":
      if(query.quellenKey) {
        model.quellenKey = queryToString(query.quellenKey);
        applyQueryFacet(query);
        applySort(query);

        let url = `${$solrURL()}main/select/?q=source.key:${model.quellenKey} AND objectType:regest AND objectProject:gpo&wt=json`;
        await executeSearch(url, query);
      }
      break;
    case "handschriften":
      if(query.handschriftenKey) {
        model.handschriftenKey = queryToString(query.handschriftenKey);
        applyQueryFacet(query);
        let url = `${$solrURL()}main/select/?q=manuscript.key:${model.handschriftenKey} AND objectType:regest AND objectProject:gpo&wt=json`;
        await executeSearch(url, query);
      }
      break;
    case "dekretale":
      if(query.dekretaleKey) {
        model.dekretaleKey = queryToString(query.dekretaleKey);
        applyQueryFacet(query);
        let url = `${$solrURL()}main/select/?q=dekretale.key:${model.dekretaleKey} AND objectType:regest AND objectProject:gpo&wt=json`;
        await executeSearch(url, query);
      }
      break;
    default:
      showError(
        createError({
          statusCode: 404,
          statusMessage: 'Not Found',
        })
      );
  }
}

await triggerSearch(route.query);

function queryToString(param: LocationQueryValue[] | string): string | null {
  if (param == null) {
    return null;
  }
  return typeof param == "string" ? param : param[0]
}

const tabChanged = (obj: any) => {
  const {old, _new} = obj;
}

const basicSearchCallback = async (searchParameters: any) => {
  navigateTo({
    path: "./" + BASIC_SEARCH_TYPE,
    query: {
      searchString: searchParameters.searchString
    }
  })
}

const sortChanged = async () => {
  navigateTo({
    path: "./" + model.currentTab,
    query: {
      ...route.query,
      sort: model.sort,
      sortOrder: model.sortOrder
    }
  });
}

async function extendedSearchCallback (searchParameters: any) {
  navigateTo({
    path: "./" + EXTENDED_SEARCH_TYPE,
    query: {
      ...searchParameters
    }
  })
}

async function pageChangedCallback(newPage: any) {
  navigateTo({
    path: "./" + model.currentTab,
    query: {
      ...route.query,
      start: (newPage - 1) * 20
    }
  });
}

async function facetClicked(facet: string, value: string|boolean) {
  let query = {} as any;

  for(let key in route.query) {
    if(key === "start") {
      continue;
    }
    query[key] = route.query[key];
  }

  if(facet!=="lost" && facet!=="fake" && typeof value === "boolean") {
    return;
  }
  value=value as string;

  switch (facet) {
    case "lost":
      query.lostFacet = (!model.facet.lostValues).toString()
      break;
    case "fake":
      query.fakeFacet = (!model.facet.fakeValues).toString()
      break;
    case "issuer":
      if (model.facet.issuerEnabledValues.indexOf(value) > -1) {
        query.issuerFacet = [...model.facet.issuerEnabledValues.filter((v: string) => v !== value)]
      } else {
        query.issuerFacet = [...model.facet.issuerEnabledValues, value];
      }
      break;
    case "recipient":
      if(model.facet.recipientEnabledValues.indexOf(value) > -1) {
        query.recipientFacet = [...model.facet.recipientEnabledValues.filter((v: string) => v !== value)]
      } else {
        query.recipientFacet = [...model.facet.recipientEnabledValues, value];
      }
      break;
    case "ueberlieferungsform":
      if(model.facet.ueberlieferungsformEnabledValues.indexOf(value) > -1) {
        query.ueberlieferungsformFacet = [...model.facet.ueberlieferungsformEnabledValues.filter((v: string) => v !== value)]
      } else {
        query.ueberlieferungsformFacet = [...model.facet.ueberlieferungsformEnabledValues, value];
      }
      break;
  }

  if (query != null) {
    navigateTo({
      path: "./" + model.currentTab,
      query: query
    });
  }
}



watch(() => route.query, async (newQueryString: LocationQuery, old: LocationQuery) => {
  triggerSearch(newQueryString);
});

</script>


<style scoped>
.hit-count {
  margin-top: 1em;
}

.clickable {
  cursor: pointer;
}

.issuer {
  font-weight: bold;
}

.facet {
  margin-top: 10px;
  margin-bottom: 10px;
}

.sort, .results {
  margin-top: 10px;
}

</style>
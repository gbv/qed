<template>
  <SovietSurvivorsLayout :headline="$t('search.label')">

    <template #content>
      <h3>{{$t("search.label")}}</h3>
      <!-- search form-->
      <div  class="row">
        <div class="col-12 col-lg-8">
          <BasicSearchForm :searchString="model.searchString" v-on:search="changeSearchString"/>
        </div>
      </div>

      <!-- result list and filter -->
      <div class="row results">

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
                      <a
                        :title="$t('search.preview')"
                        href=""
                        class="hit_option hit_download">
                        <div class="hit_icon"
                          :style="'background-image: url(&quot;' + $sovietSurviorsURL() + '/api/iiif/image/v2/thumbnail/sovsurv_mods_' + getMyCoReIdNumber(doc['id']) + '/full/!300,300/0/default.jpg&quot;)'">
                        </div>
                      </a>
                    </div>

                    <h3 class="hit_title">
                      <nuxt-link
                        :to="hitLink(doc, index)"
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
          <div class="facet">
            <h4 class="facet-title">{{ $t("search.facet.translationMode") }}</h4>
            <form class="row p-3">

              <div class="col-12">
                <div class="form-check">
                  <input class="form-check-input" name="translationMode" type="radio"
                         :checked="model.filters.translationMode == TranslationMode.ALL" id="facetTranslationAll"
                         v-on:change="changeTranslationMode(TranslationMode.ALL)">
                  <label class="form-check-label" for="facetTranslationAll">
                    {{ $t("search.facet.all") }}
                  </label>
                </div>
              </div>

              <div class="col-12">
                <div class="form-check">
                  <input class="form-check-input" name="translationMode" type="radio"
                         :checked="model.filters.translationMode == TranslationMode.TRANSLATION_ONLY" id="facetTranslationOnly"
                         v-on:change="changeTranslationMode(TranslationMode.TRANSLATION_ONLY)">
                  <label class="form-check-label" for="facetTranslationOnly">
                    {{ $t("search.facet.translationOnly") }}
                  </label>
                </div>
              </div>

              <div class="col-12">
                <div class="form-check">
                  <input class="form-check-input" name="translationMode" type="radio"
                         :checked="model.filters.translationMode == TranslationMode.ORIGINAL_ONLY" id="facetOriginalOnly"
                         v-on:change="changeTranslationMode(TranslationMode.ORIGINAL_ONLY)">
                  <label class="form-check-label" for="facetOriginalOnly">
                    {{ $t("search.facet.originalOnly") }}
                  </label>
                </div>
              </div>

            </form>


          </div>

          <div class="facet">
            <h4 class="facet-title">{{ $t("search.facet.genre") }}</h4>
            <ul class="list-group">
              <li
                v-for="genre in model.facets.genres"
                :class="model.filters.genres.indexOf(genre.name) > -1 ? 'active' : ''"
                v-on:click="clickGenreFacet(genre.name)"
                class="list-group-item facet-item d-flex justify-content-between align-items-center clickable">
                <div class="d-flex">
                  <i v-if="model.filters.genres.indexOf(genre.name) > -1" class="bi bi-check-square"></i>
                  <i v-else class="bi bi-square"></i>
                  <MODSClassification class-id="mir_genres" :categ-id="genre.name" />
                </div>
                <span class="badge badge-facet rounded-pill mt-1 ms-1">{{ genre.count }}</span>
              </li>
            </ul>
          </div>

          <div class="facet">
            <h4 class="facet-title">{{ $t("search.facet.language") }}</h4>
            <ul class="list-group">
              <li
                v-for="language in model.facets.languages"
                :class="model.filters.languages.indexOf(language.name) > -1 ? 'active' : ''"
                v-on:click="clickLanguageFacet(language.name)"
                class="list-group-item facet-item d-flex justify-content-between align-items-center clickable">
                <div class="d-flex">
                  <i v-if="model.filters.languages.indexOf(language.name) > -1" class="bi bi-check-square"></i>
                  <i v-else class="bi bi-square"></i>
                  <MODSClassification class-id="rfc5646" :categ-id="language.name" />
                </div>
                <span class="badge badge-facet rounded-pill mt-1 ms-1">{{ language.count }}</span>
              </li>
            </ul>
          </div>

        </div>
      </div>

    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">

import {LocationQuery} from "vue-router";
import {getMyCoReIdNumber} from "~/api/MyCoRe";
import {trimString} from "~/api/Utils";
import {buildSOSUSearchRequestURL, Filters, modelToQuery, queryToModel, TranslationMode} from "~/api/SearchHelper";


const {$sovietSurvivorsSolrURL} = useNuxtApp();
const {$sovietSurviorsURL} = useNuxtApp();
const route = useRoute();
const sovietSurviorsSolrURL = $sovietSurvivorsSolrURL();
const sovietSurviorsURL = $sovietSurviorsURL();

interface FacetEntry {
  name: string,
  count: number
}

const model = reactive({
  searchString: route.query.q as string || "*",
  result: null as any,
  count: 0,
  start: parseInt(route.query.start as string) || 0,
  filters: { // enabled
    genres: [],
    languages: [],
    translationMode: TranslationMode.ALL,
  } as Filters,
  facets: {
    genres: [] as FacetEntry[],
    languages: [] as FacetEntry[],
  }
});



watch(() => route.query, async (newQueryString: LocationQuery, old: LocationQuery) => {
  queryToModel(newQueryString, model);
  console.log(["searching", newQueryString]);
  await search();
});


const search = async () => {
  const url = buildSOSUSearchRequestURL(sovietSurviorsSolrURL, model.searchString, model.filters, model.start);

  model.result = await fetch(url, {
    method: "GET",
    headers: {
      "Accept": "application/json",
    }
  }).then((resp) => resp.json());

  model.count = model.result.response.numFound;
  const genreFacet = model.result?.facet_counts?.facet_fields["mods.genre"] || [];
  model.facets.genres = [];
  for (let i = 0; i < genreFacet.length; i += 2) {
    model.facets.genres.push({
      name: genreFacet[i],
      count: genreFacet[i + 1]
    });
  }

  const languageFacet = model.result?.facet_counts?.facet_fields["survivors.mods.language"] || [];
  model.facets.languages = [];
  for (let i = 0; i < languageFacet.length; i += 2) {
    model.facets.languages.push({
      name: languageFacet[i],
      count: languageFacet[i + 1]
    });
  }

}

const clickGenreFacet = async (genre: string) => {
  await navigateTo({
    query: {
      ...modelToQuery(model),
      genres: model.filters.genres.indexOf(genre) > -1 ? model.filters.genres.filter((g) => g !== genre) : [...model.filters.genres, genre],
      start: 0
    }
  })
}

const clickLanguageFacet = async (language: string) => {
  await navigateTo({
    query: {
      ...modelToQuery(model),
      languages: model.filters.languages.indexOf(language) > -1 ? model.filters.languages.filter((g) => g !== language) : [...model.filters.languages, language],
      start: 0
    }
  })
}

const pageChangedCallback = async (page: number) => {
  await navigateTo({
    query: {
      ...modelToQuery(model),
      start: (page - 1) * 20
    }
  })
}

const changeTranslationMode = async (tm: TranslationMode) => {
  await navigateTo({
    query: {
      ...modelToQuery(model),
      translationMode: tm,
      start: 0
    }
  })
}

const changeSearchString = async (event: { searchString: string }) => {
  await navigateTo({
    query: {
      ...modelToQuery(model),
      q: event.searchString,
      start: 0
    }
  })
}

const hitLink = (doc: any, index: number) => {
  const query = modelToQuery(model);
  query.start =  model.start + index;
  const queryStr = Object.keys(query).map((key) => `${key}=${query[key]}`).join("&");
  return `/soviet-survivors/documents/${getMyCoReIdNumber(doc['id'])}?${queryStr}`;
}

queryToModel(route.query, model);
await search();


</script>

<style scoped>

.clickable {
  cursor: pointer;
}

</style>
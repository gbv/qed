<template>
  <GazinLayout :headline="$t('search.label')">
    <template #content>
      <h3>{{ $t('search.label') }}</h3>
      <div class="row">
        <div class="col-12 col-lg-8">
          <BasicSearchForm :searchString="model.searchString" @search="changeSearchString" />
        </div>
      </div>

      <div class="row results">
        <div class="col-12 col-lg-8 order-2 order-lg-1 results__list">
          <div class="row g-0 result-options">
            <div class="col hit-count ms-3">
              <h2>{{ $t('search.hitCount', { count: model.count }) }}</h2>
            </div>
          </div>

          <div class="result_list">
            <div id="hit_list">
              <div class="hit_item" v-for="(doc, index) in model.result?.response?.docs" :key="doc.id">
                <div class="row hit_item_head">
                  <div class="col-12">
                    <div class="hit_counter">{{ index + 1 + model.start }}</div>
                  </div>
                </div>

                <div class="row hit_item_body">
                  <div class="col-12">
                    <h3 class="hit_title">
                      <nuxt-link :to="hitLink(doc, index)" class="main-title">
                        {{ doc['mods.title.main'] || doc['search_result_link_text'] }}
                      </nuxt-link>
                    </h3>
                    <div class="hit_abstract">
                      <div v-if="doc['mods.abstract']?.length > 0">
                        {{ trimString(doc['mods.abstract'][0]) }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <h2 v-if="model.count === 0" class="text-center mt-5">{{ $t('search.noHits') }}</h2>

          <SolrPaginator
            v-if="model.count > 0"
            :count="model.count"
            :start="model.start"
            :numPerPage="20"
            @pageChanged="pageChangedCallback"
          />
        </div>

        <div class="col-12 col-lg-4 order-1 order-lg-2 text-end text-lg-start results__facets">

          <div class="facet" v-if="model.facets.languages.length > 0">
            <h4 class="facet-title">{{ $t('search.facet.language') }}</h4>
            <ul class="list-group">
              <li
                v-for="language in model.facets.languages"
                :key="`lang-${language.name}`"
                class="list-group-item facet-item d-flex justify-content-between align-items-center clickable"
                :class="model.filters.languages.indexOf(language.name) > -1 ? 'active' : ''"
                @click="clickLanguageFacet(language.name)"
              >
                <div class="d-flex">
                  <i v-if="model.filters.languages.indexOf(language.name) > -1" class="bi bi-check-square"></i>
                  <i v-else class="bi bi-square"></i>
                  <MODSClassification :app-url="gazinURL" class-id="rfc5646" :categ-id="language.name" />
                </div>
                <span class="badge badge-facet rounded-pill mt-1 ms-1">{{ language.count }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </template>
  </GazinLayout>
</template>

<script setup lang="ts">
import { type LocationQuery } from 'vue-router';
import { getMyCoReIdNumber } from '~/api/MyCoRe';
import { trimString } from '~/api/Utils';
import { buildGazinSearchRequestURL, type GazinFilters, gazinModelToQuery, gazinQueryToModel } from '~/api/GazinSearchHelper';

const { $gazinURL, $gazinSolrURL } = useNuxtApp();
const gazinSolrURL = $gazinSolrURL();
const gazinURL = $gazinURL();
const route = useRoute();

interface FacetEntry {
  name: string;
  count: number;
}

const model = reactive({
  searchString: (route.query.q as string) || '*',
  result: null as any,
  count: 0,
  start: parseInt(route.query.start as string) || 0,
  filters: {
    genres: [],
    languages: [],
  } as GazinFilters,
  facets: {
    genres: [] as FacetEntry[],
    languages: [] as FacetEntry[],
  }
});

const search = async () => {
  const url = buildGazinSearchRequestURL(gazinSolrURL, model.searchString, model.filters, model.start);

  model.result = await fetch(url, {
    method: 'GET',
    headers: {
      Accept: 'application/json'
    }
  }).then((resp) => resp.json());

  model.count = model.result.response.numFound;

  const genreFacet = model.result?.facet_counts?.facet_fields?.['mods.genre'] || [];
  model.facets.genres = [];
  for (let i = 0; i < genreFacet.length; i += 2) {
    model.facets.genres.push({
      name: genreFacet[i],
      count: genreFacet[i + 1]
    });
  }

  const categoryFacet = model.result?.facet_counts?.facet_fields?.['category'] || [];
  model.facets.languages = [];
  for (let i = 0; i < categoryFacet.length; i += 2) {
    const value = categoryFacet[i] as string;
    if (!value?.startsWith('rfc5646:')) {
      continue;
    }
    model.facets.languages.push({
      name: value.split(':')[1],
      count: categoryFacet[i + 1]
    });
  }
};

const updateQuery = async (query: Record<string, any>) => {
  await navigateTo({
    query,
  });
};

const clickGenreFacet = async (genre: string) => {
  const genres = model.filters.genres.indexOf(genre) > -1
    ? model.filters.genres.filter((g) => g !== genre)
    : [...model.filters.genres, genre];

  await updateQuery({
    ...gazinModelToQuery(model),
    genres,
    start: '0'
  });
};

const clickLanguageFacet = async (language: string) => {
  const languages = model.filters.languages.indexOf(language) > -1
    ? model.filters.languages.filter((l) => l !== language)
    : [...model.filters.languages, language];

  await updateQuery({
    ...gazinModelToQuery(model),
    languages,
    start: '0'
  });
};

const pageChangedCallback = async (page: number) => {
  await updateQuery({
    ...gazinModelToQuery(model),
    start: ((page - 1) * 20).toString()
  });
};

const changeSearchString = async (event: { searchString: string }) => {
  await updateQuery({
    ...gazinModelToQuery(model),
    q: event.searchString,
    start: '0'
  });
};

const hitLink = (doc: any, index: number) => {
  const query = gazinModelToQuery(model);
  query.start = (model.start + index).toString();
  const queryStr = Object.keys(query)
    .map((key) => {
      const value = query[key];
      if (Array.isArray(value)) {
        return value.map((item: string) => `${key}=${item}`).join('&');
      }
      return `${key}=${value}`;
    })
    .join('&');
  return `/gazin/documents/${getMyCoReIdNumber(doc['id'])}?${queryStr}`;
};

watch(() => route.query, async (newQuery: LocationQuery) => {
  gazinQueryToModel(newQuery, model);
  await search();
});

gazinQueryToModel(route.query, model);
await search();
</script>

<style scoped>
.clickable {
  cursor: pointer;
}
</style>

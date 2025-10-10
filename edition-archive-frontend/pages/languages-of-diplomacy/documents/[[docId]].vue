<template>
  <LoDLayout>
    <template #content>
      <div class="row">
        <div class="col-12 lod-detail-view__navigation">
          <div class="top-nav d-flex row">
            <div class="col-5 text-start">
              <nuxt-link :to="data?.prev?.link" v-if="data?.prev">
                <i class="bi bi-chevron-left"></i>
                <span>{{ data?.prev?.title }}</span>
              </nuxt-link>
            </div>
            <div class="col-2 text-center" v-if="data?.counts">
              {{ $t('metadata.counts', { start: data?.counts?.start, numFound: data?.counts?.numFound }) }}
            </div>
            <div class="col-5 text-end">
              <nuxt-link :to="data?.next?.link" v-if="data?.next">
                <span>{{ data?.next?.title }}</span>
                <i class="bi bi-chevron-right"></i>
              </nuxt-link>
            </div>
          </div>
        </div>
      </div>

      <div class="row lod-detail-view__metadata">
        <div class="col-12">
          <MODSDocument :backend-url="ditavURL" v-if="data?.xml" :xml="data?.xml" :id="mycoreId" projectDocumentUrlPrefix="/languages-of-diplomacy/documents/" :filter-params="filterParams">
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

              <div class="lod-detail-view__copyrights--images">
                {{ $t("lod.metadata.copyright") }}
              </div>
            </template>
          </MODSDocument>
        </div>
      </div>
    </template>
  </LoDLayout>
</template>

<script setup lang="ts">
import {
  byName,
  findElement,
  findFirstElement,
  flattenElement,
  getAttribute, type XElement,
  XMLApi
} from '~/api/XMLApi';
import { getMyCoReId, getMyCoReIdNumber } from '~/api/MyCoRe';

import {
  buildLodSearchRequestURL,
  LodFilterParams,
  type LodFilters, lodModelToQuery,
  lodQueryToModel,
  TranslationMode
} from "~/api/LodSearchHelper";

const { $ditavURL, $ditavSolrURL } = useNuxtApp();
const route = useRoute();
const ditavURL = $ditavURL();
const ditavSolrURL = $ditavSolrURL();

const docId = route.params.docId as string;
const OBJECT_PROJECT = 'lod';

const mycoreId = getMyCoReId(OBJECT_PROJECT, parseInt(docId));

const filterParams = LodFilterParams;

interface LinkInfo {
  title: string;
  link: string;
}

const serializeQuery = (query: Record<string, any>): string => {
  return Object.keys(query)
    .map((key) => {
      const value = query[key];
      if (Array.isArray(value)) {
        return value.map((item: string) => `${key}=${item}`).join('&');
      }
      return `${key}=${value}`;
    })
    .filter(Boolean)
    .join('&');
};

const { data, error } = await useAsyncData(route.fullPath, async () => {
  const q = route.query.q as string;
  const promises: Array<Promise<any>> = [
    fetch(`${ditavURL}api/v2/objects/${mycoreId}`, {
      method: 'GET'
    })
      .then((resp) => resp.text())
      .then((text) => XMLApi(text))
  ];

  const model = {
    filters: {
      genres: [],
      languages: [],
      translationMode: TranslationMode.ALL,
    } as LodFilters,
    start: 0
  };
  lodQueryToModel(route.query, model);

  if (q) {
    const startForQuery = model.start === 0 ? model.start : model.start - 1;
    promises.push(
      fetch(buildLodSearchRequestURL(ditavSolrURL, q, model.filters, startForQuery), {
        method: 'GET',
        headers: {
          Accept: 'application/json'
        }
      }).then((resp) => resp.json())
    );
  }

  const [xml, searchResult] = await Promise.all(promises);

  if (searchResult && q) {
    const docs = searchResult.response.docs;
    const prevDoc = model.start === 0 ? undefined : docs[0];
    const nextDoc = model.start === 0 ? docs[1] : docs[2];

    const query = lodModelToQuery(model);

    let prev: LinkInfo | undefined;
    if (prevDoc) {
      query.start = (model.start - 1).toString();
      const prevQuery = serializeQuery(query);
      prev = {
        title: prevDoc['mods.title.main'] || prevDoc['search_result_link_text'],
        link: `/languages-of-diplomacy/documents/${getMyCoReIdNumber(prevDoc['id'])}?${prevQuery}`
      };
    }

    let next: LinkInfo | undefined;
    if (nextDoc) {
      query.start = (model.start + 1).toString();
      const nextQuery = serializeQuery(query);
      next = {
        title: nextDoc['mods.title.main'] || nextDoc['search_result_link_text'],
        link: `/languages-of-diplomacy/documents/${getMyCoReIdNumber(nextDoc['id'])}?${nextQuery}`
      };
    }

    return {
      xml,
      prev,
      next,
      counts: {
        start: model.start + 1,
        numFound: searchResult.response.numFound
      }
    };
  }

  return { xml };
});

const downloadLink = computed(() => {
  if(!data?.value?.xml){
    return undefined;
  }
  if (findFirstElement(data.value.xml, byName("derobject")) != null) {
    return `${ditavURL}servlets/DitavExportServlet/?id=${mycoreId}`;
  } else {
    return undefined;
  }
});


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

  return ditavURL + "rsc/viewer/" + href.value + "/" + maindoc +"?frame=true&embedded=true";

});


if (error.value) {
  showError(
    createError({
      statusCode: 404,
      statusMessage: 'Not Found'
    })
  );
}
</script>

<style>

.viewer {
  display: block;
  margin-top: 1rem;
  width: 100%;
  height: 500px;
}

</style>

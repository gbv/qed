<template>
  <GazinLayout>
    <template #content>
      <div class="row">
        <div class="col-12 gazin-detail-view__navigation">
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

      <div class="row gazin-detail-view__metadata">
        <div class="col-12">
          <MODSDocument :show-classifications="['gazin_genres']" :backend-url="gazinURL" v-if="data?.xml" :xml="data?.xml" :id="mycoreId" projectDocumentUrlPrefix="/gazin/documents/" :filter-params="filterParams" :hide-genre="true">
            <template #downloadLink>
              <MODSMetaKeyValue v-if="downloadLinkTranscription">
                <template #key>
                  {{ $t("metadata.download") }}
                </template>
                <template #value>
                  <a :href="downloadLinkTranscription" :download="`transcription-${mycoreId}.xml`">
                    <span class="bi bi-file-earmark-zip"/>{{ $t("metadata.downloadText") }}
                  </a>
                </template>
              </MODSMetaKeyValue>
            </template>
            <template #media>

              <div v-if="data.urlPresent && !data.youtube" class="sound mt-3">

                <client-only>
                  <audio :src="data.mp3OrYoutubeUrl" controls>
                    Your browser does not support the audio element.
                  </audio>
                </client-only>
                <!--
                <div class="gazin-detail-view__copyrights--images">
                  {{ $t("gazin.metadata.copyright") }}
                </div>
                -->
              </div>
              <div v-else-if="data.urlPresent && data.youtube">
                <a :href="data.mp3OrYoutubeUrl">{{ data.mp3OrYoutubeUrl }}</a>

                <!--
                <div class="gazin-detail-view__copyrights--images">
                  {{ $t("gazin.metadata.copyright") }}
                </div>
                -->
              </div>



              <div v-if="derivateInfo?.id && derivateInfo.mainDoc" class="transcript mt-3">
                <h3>{{ $t("gazin.metadata.transcription") }}</h3>
                <GazinTranscriptionSplit v-if="data.xml" :backend-url="gazinURL"
                                         :mycore-id="mycoreId" :xml="data.xml"/>
              </div>
            </template>
          </MODSDocument>
        </div>
      </div>
      <div class="row gazin-detail-view__copyrights">
        <div class="col">
          {{ $t("gazin.metadata.cite") }}
        </div>

        <div class="col-auto regest-licence text-end">
          <a href="https://creativecommons.org/licenses/by/4.0/deed.de" title="CC BY 4.0" class="no-external-mark">
            <nuxt-img src="/images/creative-commons.svg" alt="cc" />
            <nuxt-img src="/images/creative-commons-by.svg" alt="by" />

          </a>
        </div>
      </div>
    </template>
  </GazinLayout>
</template>

<script setup lang="ts">
import {
  byName,
  findElement,
  findFirstElement,
  flattenElement,
  getAttribute,
  XMLApi
} from '~/api/XMLApi';
import {getMyCoReId, getMyCoReIdNumber} from '~/api/MyCoRe';
import {
  buildGazinSearchRequestURL,
  GazinFilterParams,
  type GazinFilters,
  gazinModelToQuery,
  gazinQueryToModel,
} from '~/api/GazinSearchHelper';

const { $ditavURL, $ditavSolrURL } = useNuxtApp();
const route = useRoute();
const gazinURL = $ditavURL();
const gazinSolrURL = $ditavSolrURL();

const docId = route.params.docId as string;
const OBJECT_PROJECT = 'gzn';

const mycoreId = getMyCoReId(OBJECT_PROJECT, parseInt(docId));

const filterParams = GazinFilterParams;

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

const isYoutubeURL = (url: string|undefined): boolean => {
  if (url == null) {
    return false;
  }
  return url.includes("youtube.com") || url.includes("youtu.be");
};

const { data, error } = await useAsyncData(route.fullPath, async () => {
  const q = route.query.q as string;
  const promises: Array<Promise<any>> = [
    fetch(`${gazinURL}api/v2/objects/${mycoreId}`, {
      method: 'GET'
    })
    .then((resp) => resp.text())
    .then((text) => XMLApi(text))
  ];

  const model = {
    filters: {
      genres: [],
      languages: [],
      translations: []
    } as GazinFilters,
    start: 0
  };
  gazinQueryToModel(route.query, model);

  if (q) {
    const startForQuery = model.start === 0 ? model.start : model.start - 1;
    promises.push(
      fetch(buildGazinSearchRequestURL(gazinSolrURL, q, model.filters, startForQuery), {
        method: 'GET',
        headers: {
          Accept: 'application/json'
        }
      }).then((resp) => resp.json())
    );
  }

  const [xml, searchResult] = await Promise.all(promises);

  let mp3OrYoutubeUrl = undefined;
  let youtube: boolean = false;
  let urlPresent: boolean = false;

  if (xml != null) {
    const location = findFirstElement(xml, byName("mods:location"));

    if (location != null) {
      const urlElement = flattenElement(findFirstElement(location, byName("mods:url")));
      if (urlElement != null) {
        const urlRegexp = /https?:\/\/perspectivia\.net\/receive\/(?<objectID>[A-Za-z]+_mods_\d+)/;
        const match = urlElement.match(urlRegexp);
        const objectID = match?.groups?.objectID;
        if (objectID != null) {
          const newURL = `https://perspectivia.net/api/v2/objects/${objectID}`;
          const response2 = await fetch(newURL, {
            method: 'GET',
            headers: {
              Accept: 'application/xml'
            }
          });
          const text2 = await response2.text();
          const xml2 = await XMLApi(text2);
          const location2 = flattenElement(findFirstElement(xml2, byName("mods:location")))?.trim();

          if (isYoutubeURL(location2)) {
            urlPresent = true;
            youtube = true;
            mp3OrYoutubeUrl = location2;
          } else {
            const derobjectElement = findFirstElement(xml2, byName("derobject"));
            const maindoc = flattenElement(findFirstElement(xml2, byName("maindoc")));

            if (derobjectElement == null) {
              mp3OrYoutubeUrl = undefined;
              urlPresent = false;
            } else {
              const derivateID = getAttribute(derobjectElement, "xlink:href")?.value;
              mp3OrYoutubeUrl = `https://perspectivia.net/api/v2/objects/${objectID}/derivates/${derivateID}/contents/${maindoc}`;
              urlPresent = true;
            }
          }

        }
      }
    }
  }

  if (mp3OrYoutubeUrl == null) {
    mp3OrYoutubeUrl = undefined;
  }

  if (searchResult && q) {
    const docs = searchResult.response.docs;
    const prevDoc = model.start === 0 ? undefined : docs[0];
    const nextDoc = model.start === 0 ? docs[1] : docs[2];

    const query = gazinModelToQuery(model);

    let prev: LinkInfo | undefined;
    if (prevDoc) {
      query.start = (model.start - 1).toString();
      const prevQuery = serializeQuery(query);
      prev = {
        title: prevDoc['mods.title.main'] || prevDoc['search_result_link_text'],
        link: `/gazin/documents/${getMyCoReIdNumber(prevDoc['id'])}?${prevQuery}`
      };
    }

    let next: LinkInfo | undefined;
    if (nextDoc) {
      query.start = (model.start + 1).toString();
      const nextQuery = serializeQuery(query);
      next = {
        title: nextDoc['mods.title.main'] || nextDoc['search_result_link_text'],
        link: `/gazin/documents/${getMyCoReIdNumber(nextDoc['id'])}?${nextQuery}`
      };
    }

    return {
      xml,
      prev,
      next,
      counts: {
        start: model.start + 1,
        numFound: searchResult.response.numFound
      },
      mp3OrYoutubeUrl,
      youtube,
      urlPresent
    };
  }

  return {
    xml,
    mp3OrYoutubeUrl,
    youtube,
    urlPresent
  };
});

const derivateInfo = computed(() => {
  if (data.value?.xml == null) {
    return undefined;
  }
  let derivateElements = findElement(data.value.xml, byName("derobject"));
  if (derivateElements.length == 0) {
    return null;
  }
  const derivate = derivateElements[0]
  const id = getAttribute(derivate, "xlink:href")?.value || null;
  const mainDoc = flattenElement(findElement(derivate, byName("maindoc"))[0]);

  return {id, mainDoc};
});


const downloadLinkTranscription = computed(() => {
  if (data.value?.xml == null) {
    return undefined;
  }
  const derobjectElement = findFirstElement(data.value.xml, byName("derobject"));
  if (derobjectElement != null) {
    const maindoc = flattenElement(findFirstElement(derobjectElement, byName("maindoc")));
    let derid = getAttribute(derobjectElement, "xlink:href")?.value;
    return `${gazinURL}api/v2/objects/${mycoreId}/derivates/${derid}/contents/${maindoc}`;
  } else {
    return undefined;
  }
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
</style>

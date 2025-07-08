<template>
  <SovietSurvivorsLayout>

    <template #content>
      <CMSPage :slug="$route.path"/>

      <div v-if="$route.path=='/soviet-survivors/' && solrResponse!=null" class="row">
        <div class="col-12">
          <client-only>
            <SovietSurvivorsMapWithMetadata :center-x="20.019309"
                                            :center-y="52.114829"
                                            :solr-response="solrResponse"
                                            v-on:object-selected="objectSelected"
            >
              <template #metadata="{ solrdocs }">
                <div class="card documents_card m-3">
                  <div class="card-body">
                    <div class="row document mt-5" v-for="selectedObject in solrdocs">

                      <div v-if="selectedObject['derCount'] > 0" class="row">
                        <div class="col-12 text-center">
                          <div class="doc_thumbnail">
                            <img
                              :src=" $sovietSurviorsURL() + '/api/iiif/image/v2/thumbnail/' + selectedObject['id'] + '/full/!300,300/0/default.jpg'"
                              alt="thumbnail"
                              class="img-thumbnail">
                          </div>
                        </div>
                      </div>
                      <div class="row mt-1">
                        <div class="col-12">
                          <h3 class="text-center">
                            <nuxt-link
                              :to="hitLink(selectedObject)"
                              class="main-title">
                              {{ selectedObject["mods.title.main"] }}
                            </nuxt-link>
                          </h3>

                          <div class="abstract">
                            <div v-if="selectedObject['mods.abstract']?.length>0">
                              {{ trimString(selectedObject["mods.abstract"][0]) }}
                            </div>
                          </div>

                        </div>
                      </div>


                    </div>
                  </div>
                </div>
              </template>

            </SovietSurvivorsMapWithMetadata>
          </client-only>
        </div>
      </div>

    </template>

  </SovietSurvivorsLayout>
</template>

<script lang="ts" setup>

import {getMyCoReIdNumber} from "~/api/MyCoRe";
import {trimString} from "~/api/Utils";

const {path} = useRoute()
const {$sovietSurvivorsSolrURL} = useNuxtApp();
const solrResponse = ref(null as any);

const selectedObject = ref(null as any);

if (path === '/soviet-survivors/') {
  const {data, error} = await useAsyncData('solr-response', () => {
    return $fetch($sovietSurvivorsSolrURL() + 'mir/select?q=%2BobjectType%3A"mods"&fl=*&sort=mods.dateIssued+desc&rows=99999&fq=common.mods.coordinates.str:*&fl=common.mods.coordinates:*&wt=json&XSL.Style=xml');
  });

  if (error.value) {
    showError(
      createError({
        statusCode: 404,
        statusMessage: 'Not Found',
      })
    );
  } else {
    solrResponse.value = JSON.parse(data.value as string);
  }
}

const objectSelected = (object: any | null) => {
  if (object == null) {
    selectedObject.value = null;
    return;
  }

  selectedObject.value = object;

}

const hitLink = (doc: any) => {
  return `/soviet-survivors/documents/${getMyCoReIdNumber(doc['id'])}`;
}

</script>
<style scoped>
.documents_card {
  overflow: scroll;
  height: 500px !important;
}

.document:not(:last-child) {
  border-bottom: 1px solid;
  border-image: linear-gradient(to right, transparent, #006978, transparent) 1;
  padding-bottom: 2rem;
  margin-bottom: 2rem;
}

.abstract {
  text-align: justify;
  hyphens: auto;
  -webkit-hyphens: auto;
  -ms-hyphens: auto;
  word-break: break-word;
  /* Optional: Mindestbreite für Trennung */
  hyphenate-limit-chars: 6 3 3;
  /* Optional: Sprache für korrekte Trennung */

}


</style>

<style>
.ol-overlay-container {
  width: 30% !important;

}
</style>
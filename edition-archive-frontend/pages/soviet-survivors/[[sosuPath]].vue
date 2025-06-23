<template>
  <SovietSurvivorsLayout>

    <template #content>
      <CMSPage :slug="$route.path"/>

      <div v-if="$route.path=='/soviet-survivors/' && solrResponse!=null" class="row">
        <div class="col-8">
          <client-only>
            <SovietSurvivorsMapWithMetadata :center-x="20.019309"
                                            :center-y="52.114829"
                                            :solr-response="solrResponse"
                                            v-on:object-selected="objectSelected"
            />
          </client-only>
        </div>
        <div v-if="selectedObject != null" class="selected-object col-4">
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
              <h3>
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
    return $fetch($sovietSurvivorsSolrURL() + 'mir/select?q=%2BobjectType%3A"mods"&fl=*&sort=mods.dateIssued+desc&rows=99999&fl=common.mods.coordinates:*&wt=json&XSL.Style=xml');
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


</style>
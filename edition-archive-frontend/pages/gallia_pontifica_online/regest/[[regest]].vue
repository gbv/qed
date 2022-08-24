<template>
  <div>
    <div class="row">
      <div class="col text-center">
        <h2 class="mb-5">Gallia Pontificia Online</h2>
      </div>
    </div>
    <div class="row">
      <div class="col-3">
        <gallia-pontifica-online-menu/>
      </div>
      <div class="col-9 content" v-if="data">
        <BrowseComponent :current="parseInt(regestedIdno)" :of="browseData.count" :next-label="browseData.nextLabel"
                         :prev-label="browseData.prevLabel"
                         v-on:prevClicked="browsePrevClicked"
                         v-on:nextClicked="browseNextClicked"/>

        <section class="heading mt-5">
          <h2>{{ flattenElement(findFirstElement(data, byName("cei:idno"))) }}</h2>
          <span v-for="issuedContent in findFirstElement(data, byName('cei:issued')).content">
          <template v-if="issuedContent.name==='cei:placeName'">
            <span>{{ issuedContent.content[0] }}</span>
          </template>
          <template v-else>
            {{ flattenElement(issuedContent) }}
          </template>
         </span>
        </section>
        <section class="abstract" v-for="abstract in findElement(data, byName('cei:abstract'))">
          <template
              v-for="abstractContent in flattenElementExcept(abstract, or(byName('cei:issuer'), byName('cei:recipient'), byName('cei:persName')))">
            <template v-if="typeof abstractContent ==='string'">
              {{ abstractContent }}
            </template>
            <template v-else-if="abstractContent.name === 'cei:issuer'">
              <template v-for="issuerContent in flattenElementExcept(abstract, byName('cei:persName'))">
                <template v-if="typeof issuerContent === 'string'">
                  {{ issuerContent }}
                </template>
                <template v-else>
                  <!-- is a persName -->
                  {{ flattenElement(issuerContent) }}
                </template>
              </template>
            </template>
            <template v-else-if="abstractContent.name === 'cei:recipient'">
              {{ flattenElement(findFirstElement(abstractContent, byName("cei:persName"))) }}
            </template>
            <template v-else-if="abstractContent.name === 'cei:persName'">
              {{ flattenElement(abstractContent) }}
            </template>
          </template>
        </section>

        <section class="ueberlieferungsform"
                 v-if="findFirstElement(data, and(byName('cei:p'), byAttr('type', 'Überlieferungsform')))">
          <h3>{{ $t("regest_ueberlieferungsform") }}</h3>
          {{ flattenElement(findFirstElement(data, and(byName('cei:p'), byAttr('type', 'Überlieferungsform')))) }}
        </section>


        <section class="pontifikatPP"
                 v-for="pontifikatPP in findElement(data, and(byName('cei:p'), byAttr('type', 'PontifikatPP')))">
          <h3> {{ $t("regest_pontifikatPP") }}</h3>
          {{ flattenElement(pontifikatPP) }}
        </section>

        <section class="pontifikatAEP"
                 v-for="pontifikatAEP in findElement(data, and(byName('cei:p'), byAttr('type', 'PontifikatAEP')))">
          <h3> {{ $t("regest_pontifikatAEP") }}</h3>
          {{ flattenElement(pontifikatAEP) }}
        </section>

        <section class="ueberlieferung"
                 v-for="ueberlieferung in findElement(data, and(byName('cei:p'), byAttr('type', 'Überlieferung')))">
          <h3> {{ $t("regest_ueberlieferung") }}</h3>
          {{ flattenElement(ueberlieferung) }}
        </section>

        <section class="sachkommentar"
                 v-for="sachkommentar in findElement(data, and(byName('cei:p'), byAttr('type', 'Sachkommentar')))">
          <h3> {{ $t("regest_sachkommentar") }}</h3>
          {{ flattenElement(sachkommentar) }}
        </section>

        <hr/>
        {{ data }}
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import BrowseComponent from "~/components/BrowseComponent.vue";

const route = useRoute()
const config = useRuntimeConfig()
import {createError} from 'h3'
import {
  XMLApi,
  findFirstElement,
  findElement,
  byName,
  and,
  or,
  byAttr,
  flattenElement,
  flattenElementExcept
} from "~/api/XMLApi";

const regestedIdno = route.params.regest;

const {$solrURL, $backendURL} = useNuxtApp();

const {data, error} = await useAsyncData(`idno:${regestedIdno}`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=idno:${regestedIdno}&wt=json`)
  const json = await request.json();
  if (json.response.numFound === 0) {
    throw 404;
  }
  const regestString = json.response.docs[0]['regest.xml'];
  const doc = await XMLApi(regestString);
  return doc;
});

const {data: browseData} = await useAsyncData(`idno-count-follow:${regestedIdno}`, async () => {
  const [prevRequest, nextRequest] = await Promise.all([
    fetch(`${$solrURL()}main/select/?q=idno:[* TO ${regestedIdno}]&wt=json&rows=2&sort=idno desc`),
    fetch(`${$solrURL()}main/select/?q=idno:[${regestedIdno} TO *]&wt=json&rows=2&sort=idno asc`)
  ]);

  const [prevJson, nextJson] = await Promise.all(
      [prevRequest.json(), nextRequest.json()]
  );
  const result: any = {
    count: prevJson.response.numFound + nextJson.response.numFound - 1,
  };

  if (prevJson.response.docs.length > 1) {
    result.prevLabel = prevJson.response.docs[1].idno + "";
  }

  if (nextJson.response.docs.length > 1) {
    result.nextLabel = nextJson.response.docs[1].idno + "";
  }

  return result;
});

const browsePrevClicked = () => {
  const regestNumber = parseInt(regestedIdno as string);

  navigateTo({
    path: "./" + (regestNumber - 1),
    query: {
      ...route.query
    }
  });
}

const browseNextClicked = () => {
  const regestNumber = parseInt(regestedIdno as string);

  navigateTo({
    path: "./" + (regestNumber + 1),
    query: {
      ...route.query
    }
  });
}

if (error.value) {
  if (error.value as unknown as number === 404) {
    throwError(
        createError({
          statusCode: 404,
          statusMessage: 'Not Found',
        })
    );
  }
}

</script>
<style scoped>
.content {

}

.heading {
  text-align: center;
}

.abstract {
  text-align: left;
}

.ueberlieferungsform {

}

section {
  margin-bottom: 1em;
}
</style>
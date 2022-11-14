<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
      <h3>{{ $t("regests") }}</h3>
      <div v-if="viewModel" class="content regest-detail-view">

        <BrowseComponent :current="parseInt(regestedIdno)" :next-label="browseData.nextLabel" :of="browseData.count"
                         :prev-label="browseData.prevLabel" v-on:nextClicked="browseNextClicked"
                         v-on:indexEntered="browseIndexEntered" v-on:prevClicked="browsePrevClicked"/>

        <div class="content regest-detail-view__content">

          <div class="heading">
            <h4 class="mb-4 text-center">{{ viewModel.idno }}</h4>
            <div class="row">
              <div class="PontifikatAEP col text-start">
                <span v-if="viewModel.pontifikatAEP">
                  <template v-for="part in viewModel.pontifikatAEP">
                    <template v-if="typeof part == 'string'">{{ part }}</template>
                    <GalliaPontificaOnlineRegestPerson v-else :person="part"/>
                  </template>
                </span>
              </div>
              <div class="issued col text-center">
                <template v-for="part in viewModel.issued">
                  <template v-if="typeof part == 'string'">{{ part }}</template>
                  <GalliaPontificaOnlineRegestPlace v-else :place="part"/>
                </template>
              </div>
              <div class="PontifikatPP col text-end">
                <span v-if="viewModel.pontifikatPP">
                  <template v-for="part in viewModel.pontifikatPP">
                    <template v-if="typeof part == 'string'">{{ part }}</template>
                    <GalliaPontificaOnlineRegestPerson v-else :person="part"/>
                  </template>
                </span>
              </div>
            </div>
          </div>

          <div v-if="viewModel.abstract" class="section abstract">
            <GalliaPontificaOnlineRegestMixedContent :contents="viewModel.abstract.content"/>
            <span class="fst-italic" v-if="viewModel.incipit">
              — {{ viewModel.incipit }}.
            </span>
          </div>

          <div v-if="viewModel.witListPar!= null && viewModel.witListPar.content.length>0" class="section witlist">
            <h5>{{ $t("regest_ueberlieferung") }}</h5>
            <span>
              <GalliaPontificaOnlineRegestMixedContent :contents="viewModel.witListPar.content"/>
            </span>
          </div>

          <div v-if="viewModel.ueberlieferung!= null && viewModel.ueberlieferung.content.length>0"
               class="section ueberlieferung">
            <h5>{{ $t("regest_ueberlieferung") }}</h5>
            <div>
              <GalliaPontificaOnlineRegestMixedContent :contents="viewModel.ueberlieferung.content"/>
            </div>
          </div>

          <div v-if="viewModel.listBiblEdition!=null && viewModel.listBiblEdition.content.length>0"
               class="section listBiblEdition">
            <h5>{{ $t("regest_editionen") }}</h5>
            <div>
              <GalliaPontificaOnlineRegestMixedContent :contents="viewModel.listBiblEdition.content"/>
            </div>
          </div>

          <!-- Erwähnungen -->
          <div v-if="viewModel.erwaehnungen != null && viewModel.erwaehnungen.content.length>0" class="section listBiblRegest">
            <h5>{{ $t("regest_erwaehnungen") }}</h5>
            <div>
              <GalliaPontificaOnlineRegestMixedContent :contents="viewModel.erwaehnungen.content"/>
            </div>
          </div>

          <!-- Regesten -->
          <div v-if="viewModel.listBiblRegest != null && viewModel.listBiblRegest.content.length>0"
               class="section listBiblRegest">
            <h5>{{ $t("regest_regests") }}</h5>
            <div>
              <GalliaPontificaOnlineRegestMixedContent :contents="viewModel.listBiblRegest.content"/>
            </div>
          </div>


          <!-- Sachkomentar -->
          <div v-if="viewModel.sachkommentar!=null && viewModel.sachkommentar.content.length>0" class="section sachkommentar">
            <h5>{{ $t("regest_sachkommentar") }}</h5>
            <div>
              <GalliaPontificaOnlineRegestMixedContent :contents="viewModel.sachkommentar.content"/>
            </div>
          </div>
        </div>

        <div class="row regest-detail-view__footer">
          <div class="col footer-persons">
            <span class="auhor">
              {{ $t("author") }}: Ludwig Falkenstein
            </span>
            <span class="publisher">
              {{ $t("publisher") }}: Rolf Große
            </span>
            <span class="editor">
              {{ $t("editor") }}: Robert Friedrich und Sebastian Gensicke
            </span>
          </div>
          <div class="col regest-licence text-end">
            <a href="https://creativecommons.org/licenses/by-sa/4.0/deed.de" title="CC BY-SA 4.0" class="no-external-mark">
              <nuxt-img src="/images/creative-commons.svg" alt="cc" />
              <nuxt-img src="/images/creative-commons-by.svg" alt="by" />
              <nuxt-img src="/images/creative-commons-sa.svg" alt="sa" />
            </a>
          </div>
        </div>
      </div>
    </template>

    <template #menu>
      <GalliaPontificaOnlineMenu/>
    </template>

  </GalliaPontificaOnlineLayout>
</template>

<script lang="ts" setup>
import BrowseComponent from "~/components/BrowseBar.vue";
import {createError} from 'h3'
import {XMLApi} from "~/api/XMLApi";
import {
  XElement,
  XText,
  findFirstElement,
  byName,
  flattenElementExcept,
  flattenElement,
  or,
  findElement,
  getAttribute,
  byAttr,
  and
} from "@mycore-org/xml-json-api"

const route = useRoute()
const config = useRuntimeConfig()

interface RegestViewModel {
  pontifikatPP: Array<XElement | string>;
  pontifikatAEP: Array<XElement | string>;
  issued: Array<XElement | string>;
  idno: string;
  witListPar?: XElement;
  abstract?: XElement;
  incipit?: string;
  ueberlieferung?: XElement;
  listBiblEdition?: XElement;
  listBiblRegest?: XElement;
  erwaehnungen?: XElement;
  sachkommentar?: XElement;
}

interface RegestWitness {
  msIdentifier?: string; // the id
  msIdentifierLabel?: string; // the label
  ref?: string; // cei:ref
}


const regestedIdno: string = typeof route.params.regest == "string" ? route.params.regest : route.params.regest[0];

const {$solrURL, $backendURL} = useNuxtApp();

const {data: viewModel, error} = await useAsyncData(`idno:${regestedIdno}`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=idno:${regestedIdno}&wt=json`)
  const json = await request.json();
  if (json.response.numFound === 0) {
    throw 404;
  }
  const currentJsonDoc = json.response.docs[0];
  const regestString = currentJsonDoc['regest.xml'];
  const doc = await XMLApi(regestString);

  const vm: RegestViewModel = {} as RegestViewModel

  vm.pontifikatAEP = flattenElementExcept(findFirstElement(doc, and(byName('cei:p'), byAttr('type', 'PontifikatAEP'))), byName('cei:persName'));

  vm.pontifikatPP = flattenElementExcept(findFirstElement(doc, and(byName('cei:p'), byAttr('type', 'PontifikatPP'))), byName('cei:persName'));

  vm.issued = flattenElementExcept(findFirstElement(doc, byName('cei:issued')), byName('cei:placeName'));

  vm.idno = flattenElement(findFirstElement(doc, byName("cei:idno")));

  vm.abstract = findFirstElement(doc, byName("cei:abstract"));

  const incipit = findFirstElement(doc, byName("cei:incipit"));
  if (incipit != null) {
    vm.incipit = flattenElement(incipit);
  }
  vm.witListPar = findFirstElement(doc, byName("cei:witListPar"));
  vm.erwaehnungen = findFirstElement(doc, and(byName("cei:p"), byAttr('type', 'Erwähnungen')));
  vm.listBiblEdition = findFirstElement(doc, byName("cei:listBiblEdition"));
  vm.listBiblRegest = findFirstElement(doc, byName("cei:listBiblRegest"));
  vm.sachkommentar = findFirstElement(doc, and(byName("cei:p"), byAttr('type', 'Sachkommentar')));
  vm.ueberlieferung = findFirstElement(doc, and(byName("cei:p"), byAttr('type', 'Überlieferung')));

  return vm;
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

const browseIndexEntered = (index: number) => {
  navigateTo({
    path: "./" + (index),
    query: {
      ...route.query
    }
  });
}


if (error.value) {
  console.error(error.value);
  if (error.value as unknown as number === 404) {
    throwError(
      createError({
        statusCode: 404,
        statusMessage: 'Not Found',
      })
    );
  } else {
    throwError(
      createError({
        statusCode: 500,
        statusMessage: error.value+ "",
      })
    );
  }
}
</script>

<style scoped>
</style>
<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
      <h3>{{ $t("regests") }}</h3>
      <div v-if="viewModel" class="content regest-detail-view">

        <BrowseComponent :current="parseInt(regestedIdno)" :next-label="browseData.nextLabel" :of="browseData.count"
                         :prev-label="browseData.prevLabel" v-on:nextClicked="browseNextClicked"
                         v-on:prevClicked="browsePrevClicked"/>

        <div class="content regest-detail-view__content">

          <div class="heading">
            <h4 class="mb-4 text-center">{{ viewModel.idno }}</h4>
            <div class="row">
              <div  class="PontifikatPP col text-start">
                <span v-if="viewModel.pontifikatPP_text">
                  {{ viewModel.pontifikatPP_text }}
                </span>
              </div>
              <div class="issued col text-center">
                <span v-if="viewModel.issued">
                  {{ viewModel.issued }}
                </span>
              </div>
              <div class="PontifikatAEP col text-end">
                <span v-if="viewModel.pontifikatAEP_text">
                  {{ viewModel.pontifikatAEP_text }}
                </span>
              </div>
            </div>
          </div>

          <div v-if="viewModel.abstract" class="section abstract">
            <template v-for="part in viewModel.abstract">
              <template v-if="typeof part != 'string'">
                <span v-if="part.name==='cei:persName'" class="person">{{ flattenElement(part) }}</span>
                <span v-if="part.name==='cei:placeName'" class="place">{{ flattenElement(part) }}</span>
              </template>
              <template v-else>
                {{ part }}
              </template>
            </template>
            <span class="fst-italic" v-if="viewModel.incipit">
              — {{ viewModel.incipit }}.
            </span>
          </div>



          <div v-if="Object.keys(viewModel.witlist).length>0" class="section witlist">
            <h5>{{ $t("regest_ueberlieferung") }}</h5>
            <dl>
              <template v-for="(obj, head)  in viewModel.witlist">
                <dt>{{ head }}</dt>
                <dd>
                  <template v-for="wit in viewModel.witlist[head]">
                    {{ wit.msIdentifierLabel || "" }} {{ wit.ref || "" }}<br/>
                  </template>
                </dd>
              </template>
            </dl>
          </div>

          <div v-if="viewModel.listBiblEdition.length>0" class="section listBiblEdition">
            <h5>{{ $t("regest_editionen") }}</h5>
            <div>
              <template v-for="bibl in viewModel.listBiblEdition">
                <GalliaPontificaOnlineRegestBibl v-if="typeof bibl !=='string'" :bibl="bibl" />
                <span v-else class="xxx">{{bibl}}</span>
              </template>
            </div>
          </div>

          <!-- Erwähnungen -->
          <div v-if="viewModel.erwaehnungen.length>0" class="section listBiblRegest">
            <h5>{{ $t("regest_erwaehnungen") }}</h5>
            <div>
              <template v-for="bibl in viewModel.erwaehnungen">
                <GalliaPontificaOnlineRegestBibl v-if="typeof bibl !=='string'" :bibl="bibl" />
                <span v-else>{{bibl}}</span>
              </template>
            </div>
          </div>

          <!-- Regesten -->
          <div v-if="viewModel.listBiblRegest.length>0" class="section listBiblRegest">
            <h5>{{ $t("regest_regests") }}</h5>
            <div>
              <template v-for="bibl in viewModel.listBiblRegest">
                <GalliaPontificaOnlineRegestBibl v-if="typeof bibl !=='string'" :bibl="bibl" />
                <span v-else>{{bibl}}</span>
              </template>
            </div>
          </div>


          <!-- Sachkomentar -->
          <div v-if="viewModel.sachkommentar.length>0" class="section sachkommentar">
            <h5>{{ $t("regest_sachkommentar") }}</h5>
            <div>
              <template v-for="bibl in viewModel.sachkommentar">
                <GalliaPontificaOnlineRegestBibl v-if="typeof bibl !=='string'" :bibl="bibl" />
                <span v-else>{{bibl}}</span>
              </template>
            </div>
          </div>

          <!--
          <div class="section ueberlieferungsform"
                   v-if="findFirstElement(data, and(byName('cei:p'), byAttr('type', 'Überlieferungsform')))">
            <h5>{{ $t("regest_ueberlieferungsform") }}</h5>
            {{ flattenElement(findFirstElement(data, and(byName('cei:p'), byAttr('type', 'Überlieferungsform')))) }}
          </div>


          <div class="pontifikatPP section"
                   v-for="pontifikatPP in findElement(data, and(byName('cei:p'), byAttr('type', 'PontifikatPP')))">
            <h5>{{ $t("regest_pontifikatPP") }}</h5>
            {{ flattenElement(pontifikatPP) }}
          </div>

          <div class="pontifikatAEP section"
                   v-for="pontifikatAEP in findElement(data, and(byName('cei:p'), byAttr('type', 'PontifikatAEP')))">
            <h5>{{ $t("regest_pontifikatAEP") }}</h5>
            {{ flattenElement(pontifikatAEP) }}
          </div>

          <div class="ueberlieferung section"
                   v-for="ueberlieferung in findElement(data, and(byName('cei:p'), byAttr('type', 'Überlieferung')))">
            <h5>{{ $t("regest_ueberlieferung") }}</h5>
            {{ flattenElement(ueberlieferung) }}
          </div>

          <div class="sachkommentar section"
                   v-for="sachkommentar in findElement(data, and(byName('cei:p'), byAttr('type', 'Sachkommentar')))">
            <h5>{{ $t("regest_sachkommentar") }}</h5>
            {{ flattenElement(sachkommentar) }}
          </div>
          -->
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
  pontifikatPP_text: string;
  pontifikatPP_obj: string;
  pontifikatAEP_text: string;
  pontifikatAEP_obj: string;
  issued: string;
  idno: string;
  witlist: Record<string, Array<RegestWitness>>
  abstract: Array<string | XElement>
  incipit?: string;
  listBiblEdition: Array<XElement | string>
  listBiblRegest: Array<XElement | string>
  erwaehnungen: Array<XElement | string>
  sachkommentar: Array<XElement | string>
}

interface RegestWitness {
  msIdentifier?: string; // the id
  msIdentifierLabel?: string; // the label
  ref?: string; // cei:ref
}


const regestedIdno: string = typeof route.params.regest == "string" ? route.params.regest : route.params.regest[0];

const {$solrURL, $backendURL} = useNuxtApp();

function extractListBibl(listBiblElement: XElement, list: Array<XElement|string>) {
  if (listBiblElement != null) {

    const biblList = flattenElementExcept(listBiblElement, byName("cei:bibl"));
    biblList.forEach(le => list.push(le));
  }
}

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

  vm.pontifikatAEP_obj = currentJsonDoc["pontifikatAEP.obj"];
  vm.pontifikatAEP_text = currentJsonDoc["pontifikatAEP"][0];

  vm.pontifikatPP_obj = currentJsonDoc["pontifikatPP.obj"];
  vm.pontifikatPP_text = currentJsonDoc["pontifikatPP"][0];

  vm.issued = currentJsonDoc["issued.text"][0];
  vm.idno = flattenElement(findFirstElement(doc, byName("cei:idno")));

  const abstractElement = findFirstElement(doc, byName("cei:abstract"));
  vm.abstract = flattenElementExcept(abstractElement, or(byName("cei:persName"), byName("cei:placeName")));

  const incipit = findFirstElement(doc, byName("cei:incipit"));
  if (incipit != null) {
    vm.incipit = flattenElement(incipit);
  }

  const witlistElement = findFirstElement(doc, byName("cei:witListPar"));

  vm.witlist = {};
  if (witlistElement != null) {
    const headWitnessArr = findElement(witlistElement, or(byName("cei:head"), byName("cei:witness")))
    let lastHead: string = null
    for (let i = 0; i < headWitnessArr.length; i++) {
      if (headWitnessArr[i].name == 'cei:head') {
        const headElement = headWitnessArr[i];
        const head = headElement == null ? null : flattenElement(headElement);
        lastHead = head;
        vm.witlist[head] = new Array<RegestWitness>();
      } else {
        const witElement = headWitnessArr[i];
        const msIdentifierElement = witElement == null ? null : findFirstElement(witElement, byName("cei:msIdentifier"));
        const msIdentifier = msIdentifierElement == null ? null : getAttribute(msIdentifierElement, "n").value;
        const msIdentifierLabel = flattenElement(msIdentifierElement);
        const refElement = witElement == null ? null : findFirstElement(witElement, byName("cei:ref"));
        const ref = refElement == null ? null : flattenElement(refElement);
        const wilistEntry: RegestWitness = { msIdentifier, msIdentifierLabel, ref};
        if(vm.witlist[lastHead] !== undefined ) {
          vm.witlist[lastHead].push(wilistEntry);
        } else {
          const missinghead = "-";
          if(vm.witlist[missinghead]==undefined){
            vm.witlist[missinghead] = new Array<RegestWitness>();
          }
          vm.witlist[missinghead].push(wilistEntry);
        }
      }
    }
  }

  vm.erwaehnungen = [];
  const erwaehnungenElement = findFirstElement(doc, and(byName("cei:p"), byAttr('type', 'Erwähnungen')));
  extractListBibl(erwaehnungenElement, vm.erwaehnungen);


  vm.listBiblEdition = [];
  const listBiblEditionElement = findFirstElement(doc, byName("cei:listBiblEdition"));
  extractListBibl(listBiblEditionElement, vm.listBiblEdition);

  vm.listBiblRegest = [];
  const listBiblRegestElement = findFirstElement(doc, byName("cei:listBiblRegest"));
  extractListBibl(listBiblRegestElement, vm.listBiblRegest);

  vm.sachkommentar = [];
  const sachkommentarElement = findFirstElement(doc, and(byName("cei:p"), byAttr('type', 'Sachkommentar')));
  extractListBibl(sachkommentarElement, vm.sachkommentar);

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
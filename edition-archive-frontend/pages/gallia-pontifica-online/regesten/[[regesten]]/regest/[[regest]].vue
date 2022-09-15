<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
      <div v-if="viewModel" class="content">
        <BrowseComponent :current="parseInt(regestedIdno)" :next-label="browseData.nextLabel" :of="browseData.count"
                         :prev-label="browseData.prevLabel" v-on:nextClicked="browseNextClicked"
                         v-on:prevClicked="browsePrevClicked"/>

        <section class="heading mt-5">
          <h2>{{ viewModel.idno }}</h2>
          <div class="row">
            <div v-if="viewModel.pontifikatPP_text" class="PontifikatPP col">
              {{ viewModel.pontifikatPP_text }}
            </div>
            <div v-if="viewModel.issued" class="issued col">
              {{ viewModel.issued }}
            </div>
            <div v-if="viewModel.pontifikatAEP_text" class="PontifikatAEP col">
              {{ viewModel.pontifikatAEP_text }}
            </div>
          </div>
        </section>

        <section v-if="viewModel.abstract" class="abstract">
          <template v-for="part in viewModel.abstract">
            <template v-if="typeof part != 'string'">
              <span v-if="part.name==='cei:persName'" class="person">{{ flattenElement(part) }}</span>
              <span v-if="part.name==='cei:placeName'" class="place">{{ flattenElement(part) }}</span>
            </template>
            <template v-else>
              {{ part }}
            </template>
          </template>
        </section>


        <section v-if="Object.keys(viewModel.witlist).length>0" class="witlist">
          <h3>{{ $t("regest_ueberlieferungen") }}</h3>
          <dl>
            <template v-for="(obj, head)  in viewModel.witlist">
              <dt>{{ head }}</dt>
              <dd>
                <template v-for="wit in viewModel.witlist[head]">
                  {{ wit.msIdentifier || "" }} {{ wit.msIdentifierLabel || "" }} {{ wit.ref || "" }}<br/>
                </template>
              </dd>
            </template>
          </dl>
        </section>

        <section v-if="viewModel.listBiblEdition.length>0" class="listBiblEdition">
          <h3>{{ $t("regest_editionen") }}</h3>
          <div>
            <template v-for="bibl in viewModel.listBiblEdition">
              <GalliaPontificaOnlineRegestBibl v-if="typeof bibl !=='string'" :bibl="bibl" />
              <span v-else>{{bibl}}</span>
            </template>
          </div>
        </section>

        <!-- Erwähnungen -->


        <!-- Regesten -->
        <section v-if="viewModel.listBiblRegest.length>0" class="listBiblRegest">
          <h3>{{ $t("regest_regests") }}</h3>
          <div>
            <template v-for="bibl in viewModel.listBiblRegest">
              <GalliaPontificaOnlineRegestBibl v-if="typeof bibl !=='string'" :bibl="bibl" />
              <span v-else>{{bibl}}</span>
            </template>
          </div>
        </section>


        <!-- Sachkomentar -->
        <section v-if="viewModel.sachkommentar.length>0" class="sachkommentar">
          <h3>{{ $t("regest_sachkommentar") }}</h3>
          <div>
            <template v-for="bibl in viewModel.sachkommentar">
              <GalliaPontificaOnlineRegestBibl v-if="typeof bibl !=='string'" :bibl="bibl" />
              <span v-else>{{bibl}}</span>
            </template>
          </div>
        </section>
        <!--

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
        </section> -->


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
  listBiblEdition: Array<XElement|string>
  listBiblRegest: Array<XElement|string>
  sachkommentar: Array<XElement|string>
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

  const witlistElement = findFirstElement(doc, byName("cei:witListPar"));

  vm.witlist = {};
  if (witlistElement != null) {
    const headWitnessArr = findElement(witlistElement, or(byName("cei:head"), byName("cei:witness")))
    let lastHead:string = null
    for (let i = 0; i < headWitnessArr.length; i++) {
      if(headWitnessArr[i].name=='cei:head') {
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
  console.log(error.value);
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

.PontifikatPP {
  float: left;
}

.PontifikatAEP {
  float: right;
}

.issued {
  clear: both;
}
</style>
<template>
  <GalliaPontificiaOnlineLayout>
    <template #content>
      <h3>{{ $t("gpo.pages.regest.headline") }}</h3>
      <div v-if="viewModel" class="content regest-detail-view">

        <BrowseComponent :current="parseInt(regestedIdno)" :next-label="viewModel.nextLabel || ''" :of="viewModel.count || 0"
                         :prev-label="viewModel.prevLabel||''" v-on:nextClicked="browseNextClicked"
                         v-on:indexEntered="browseIndexEntered" v-on:prevClicked="browsePrevClicked"/>

        <div class="content regest-detail-view__content">

          <div class="heading">
            <h4 class="mb-4 text-center">{{ viewModel.idno }}</h4>
            <div class="row">
              <div class="PontifikatAEP col text-start">
                <span v-if="viewModel.pontifikatAEP">
                  <template v-for="part in viewModel.pontifikatAEP">
                    <template v-if="typeof part == 'string'">{{ part }}</template>
                    <GalliaPontificiaOnlineRegestPerson v-else :person="part"/>
                  </template>
                </span>
              </div>
              <div class="issued col text-center">
                <span v-if="viewModel.issued!=null && viewModel.issued?.content?.length>0">
                  <GalliaPontificiaOnlineRegestMixedContent :contents="viewModel.issued.content"/>
                </span>
              </div>
              <div class="PontifikatPP col text-end">
                <span v-if="viewModel.pontifikatPP">
                  <template v-for="part in viewModel.pontifikatPP">
                    <template v-if="typeof part == 'string'">{{ part }}</template>
                    <GalliaPontificiaOnlineRegestPerson v-else :person="part"/>
                  </template>
                </span>
              </div>
            </div>
          </div>

          <div v-if="viewModel.abstract" class="section abstract">
            <GalliaPontificiaOnlineRegestMixedContent :contents="viewModel.abstract.content"/>
            <span v-if="viewModel.incipit!=null && viewModel.incipit.length>0">
              — <span v-for="(incipit,index) in viewModel.incipit"><GalliaPontificiaOnlineRegestMixedContent  :contents="incipit.content" /><template v-if="!(index+1==viewModel.incipit.length)">; </template> </span>
              <template v-if="!(flattenElement(viewModel.incipit[viewModel.incipit.length-1])||'').endsWith('.')">.</template>
            </span>
          </div>

          <div v-if="viewModel?.dekretale?.content?.length !== undefined && viewModel.dekretale.content.length>0" class="section dekretale">
            <h5>{{ $t("gpo.pages.regest.dekretale") }}</h5>
            <GalliaPontificiaOnlineRegestMixedContent v-if="viewModel.dekretale!=null && viewModel.dekretale.content.length>0" :contents="viewModel.dekretale.content"/>
          </div>


          <div v-if="viewModel.witnessOrig != null && viewModel.witnessOrig.content.length>0">
            <div class="section">
              <h5>{{ $t("gpo.pages.regest.original") }}</h5>
              <GalliaPontificiaOnlineRegestMixedContent v-if="viewModel.witnessOrig != null && viewModel.witnessOrig.content.length>0" :contents="viewModel.witnessOrig.content"/>
            </div>
          </div>

          <div v-if="viewModel.witListPar!= null && viewModel.witListPar.content.length>0" class="section witlist">
            <h5>{{ $t("gpo.pages.regest.kopialeUeberlieferung") }}</h5>
            <span>
              <GalliaPontificiaOnlineRegestMixedContent v-if="viewModel.witListPar!= null && viewModel.witListPar.content.length>0" :contents="viewModel.witListPar.content"/>
            </span>
          </div>

          <div v-if="viewModel.ueberlieferung!= null && viewModel.ueberlieferung.content.length>0"
               class="section ueberlieferung">
            <h5>{{ $t("gpo.pages.regest.ueberlieferung") }}</h5>
            <div>
              <GalliaPontificiaOnlineRegestMixedContent :contents="viewModel.ueberlieferung.content"/>
            </div>
          </div>

          <div v-if="viewModel.listBiblEdition!=null && viewModel.listBiblEdition.content.length>0"
               class="section listBiblEdition">
            <h5>{{ $t("gpo.pages.regest.editionen") }}</h5>
            <div>
              <GalliaPontificiaOnlineRegestMixedContent :contents="viewModel.listBiblEdition.content"/>
            </div>
          </div>

          <!-- Erwähnungen -->
          <div v-if="viewModel.erwaehnungen != null && viewModel.erwaehnungen.content.length>0" class="section listBiblRegest">
            <h5>{{ $t("gpo.pages.regest.erwaehnungen") }}</h5>
            <div>
              <GalliaPontificiaOnlineRegestMixedContent :contents="viewModel.erwaehnungen.content"/>
            </div>
          </div>

          <!-- Regesten -->
          <div v-if="viewModel.listBiblRegest != null && viewModel.listBiblRegest.content.length>0"
               class="section listBiblRegest">
            <h5>{{ $t("gpo.pages.regest.regests") }}</h5>
            <div>
              <GalliaPontificiaOnlineRegestMixedContent :contents="viewModel.listBiblRegest.content"/>
            </div>
          </div>


          <!-- Sachkomentar -->
          <div v-if="viewModel.sachkommentar!=null && viewModel.sachkommentar.length>0" class="section sachkommentar">
            <h5>{{ $t("gpo.pages.regest.sachkommentar") }}</h5>
            <div v-for="sachkommentar in viewModel.sachkommentar">
              <GalliaPontificiaOnlineRegestMixedContent :contents="sachkommentar.content"/>
            </div>
          </div>
        </div>

        <div class="row regest-detail-view__footer">
          <div class="col footer-persons">
            <span :class="`cite cite__${citeForm.citeVersion}`"
                  :title="$t(citeForm.citeVersion=='short' ?'gpo.pages.regest.cite.switch.long.title': 'gpo.pages.regest.cite.switch.long.title')">
              <template v-if="citeForm.citeVersion=='short'">
                {{ $t("gpo.pages.regest.cite.short") }}
              </template>
              <template v-else>
               {{ $t("gpo.pages.regest.cite.long") }}
              </template>
              {{ regestedIdno }}.
              <a :href="$t('gpo.pages.regest.cite.url')+regestedIdno">
                {{ $t("gpo.pages.regest.cite.url") }}{{ regestedIdno }}
              </a>
            </span>
            <span v-on:click.prevent="toggleCiteVersion()" class="badge rounded-pill cite-switch"
                  :title="$t(citeForm.citeVersion!=='short' ? 'gpo.pages.regest.cite.switch.short.title': 'gpo.pages.regest.cite.switch.long.title')">
                {{ $t(citeForm.citeVersion !== 'short' ?
                  "gpo.pages.regest.cite.switch.short.text" : "gpo.pages.regest.cite.switch.long.text")
                }}
            </span>
          </div>

          <div class="col-auto regest-licence text-end">
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
      <GalliaPontificiaOnlineMenu/>
    </template>

  </GalliaPontificiaOnlineLayout>
</template>

<script lang="ts" setup>
import BrowseComponent from "~/components/BrowseBar.vue";
import {XMLApi} from "~/api/XMLApi";
import {
  XElement,
  XText,
  findFirstElement,
  byName,
  flattenElementExcept,
  flattenElement,
  findElement,
  byAttr,
  and, XNode
} from "@mycore-org/xml-json-api"

const route = useRoute()
const config = useRuntimeConfig()

interface RegestViewModel {
  nextLabel: string | null;
  prevLabel: string | null;
  count: number | null;
  pontifikatPP: Array<XElement | string> | null;
  pontifikatAEP: Array<XElement | string> | null;
  issued: XElement | null;
  idno: string | null;
  witListPar: XElement | null;
  abstract: XElement | null;
  incipit: Array<XElement> | null;
  dekretale: XElement | null;
  ueberlieferung: XElement | null;
  listBiblEdition: XElement | null;
  listBiblRegest: XElement|null;
  erwaehnungen: XElement|null;
  sachkommentar: Array<XElement>|null;
  witnessOrig: XElement|null;
}



const regestedIdno: string = typeof route.params.regest == "string" ? route.params.regest : route.params.regest[0];

const {$solrURL, $backendURL} = useNuxtApp();


const traverse = (element: XElement, nodeHandler:(node:XNode)=>void) => {
  element.content.forEach((node) => {
    nodeHandler(node);
    if (node.type === "Element") {
      traverse(node, nodeHandler);
    }
  });
};

const {data: viewModel, error} = await useAsyncData(`idno:${regestedIdno}`, async () => {
  const [request, countRequest] = await Promise.all([
    fetch(`${$solrURL()}main/select/?q=idno:${regestedIdno}&wt=json`),
    fetch(`${$solrURL()}main/select/?q=objectType:regest&wt=json&rows=0`)]);

  const [json, countJson] = await Promise.all([request.json(), countRequest.json()]);

  if (json.response.numFound === 0) {
    throw 404;
  }
  const currentJsonDoc = json.response.docs[0];
  const regestString = currentJsonDoc['regest.xml'];
  const doc = await XMLApi(regestString);

  const vm: RegestViewModel = {} as RegestViewModel

  vm.prevLabel = parseInt(regestedIdno) > 1 ? (parseInt(regestedIdno) - 1).toString() : null;

  vm.nextLabel = parseInt(regestedIdno) < countJson.response.numFound ? (parseInt(regestedIdno) + 1).toString() : null;

  vm.count = countJson.response.numFound;

  const aep = findFirstElement(doc, and(byName('cei:p'), byAttr('type', 'PontifikatAEP')));
  vm.pontifikatAEP = aep !== null ? flattenElementExcept(aep, byName('cei:persName')) : null;

  const pp = findFirstElement(doc, and(byName('cei:p'), byAttr('type', 'PontifikatPP')));
  vm.pontifikatPP = pp !== null ? flattenElementExcept(pp, byName('cei:persName')) : null;

  vm.issued = findFirstElement(doc, byName('cei:issued'));

  vm.witnessOrig = findFirstElement(doc, byName('cei:witnessOrig'));

  vm.idno = flattenElement(findFirstElement(doc, byName("cei:idno")));

  vm.abstract = findFirstElement(doc, byName("cei:abstract"));

  vm.dekretale = findFirstElement(doc, and(byName("cei:p"), byAttr("type", "Dekretale")));

  vm.incipit = findElement(doc, byName("cei:incipit"));
  vm.incipit.forEach((incipit) => {
    traverse(incipit, (node)=> {
      if (node.type == "Text") {
        (node as XText).text = (node as XText).text.trim();
      }
    })
  });



  vm.witListPar = findFirstElement(doc, byName("cei:witListPar"));
  vm.erwaehnungen = findFirstElement(doc, and(byName("cei:p"), byAttr('type', 'Erwähnungen')));
  vm.listBiblEdition = findFirstElement(doc, byName("cei:listBiblEdition"));
  vm.listBiblRegest = findFirstElement(doc, byName("cei:listBiblRegest"));
  vm.sachkommentar = findElement(doc, and(byName("cei:p"), byAttr('type', 'Sachkommentar')));
  vm.ueberlieferung = findFirstElement(doc, and(byName("cei:p"), byAttr('type', 'Überlieferung')));

  return vm;
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
    showError(
      createError({
        statusCode: 404,
        statusMessage: 'Not Found',
      })
    );
  } else {
    showError(
      createError({
        statusCode: 500,
        statusMessage: error.value+ "",
      })
    );
  }
}

const citeForm = reactive({
  citeVersion: "short" as "short" | "long",
});

function toggleCiteVersion() {
  citeForm.citeVersion = citeForm.citeVersion == "short" ? "long" : "short";
}
</script>

<style scoped>

</style>

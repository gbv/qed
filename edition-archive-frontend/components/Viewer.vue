<template>
  <div class="viewer">
    <nav class="nav nav-tabs viewer-nav">
      <a :class="`nav-link${ model.viewMode == 'single' ? ' active':''}`"
         href="#"
         v-on:click.prevent="changeViewMode('single')"
      >Einzelansicht</a>
      <a :class="`nav-link${ model.viewMode == 'dual' ? ' active':''}`"
         href="#"
         v-on:click.prevent="changeViewMode('dual')"
      >Doppelansicht</a>
      <a :class="`nav-link${ model.viewMode == 'xml' ? ' active':''}`"
         href="#"
         v-on:click.prevent="changeViewMode('xml')"
      >XML</a>
    </nav>
    <rows class="viewer-content" :order="['img', 'tei','xml']">
      <template v-if="model.viewMode == 'dual'" #img>
        <div class="viewer-image-content">
          <div class="viewer-image-wrapper">
            <nuxt-picture
              v-if="model.currentImage"
              :src="imageSrc"
              :imgAttrs="{ class: 'viewer-image', alt: 'Current page image' }"
            />

            <div v-else>
              <em>Keine Seitenabbildung verfügbar</em>
            </div>

            <!-- Maximize button (sichtbar nur wenn ein Bild vorhanden ist) -->
            <button
              v-if="model.currentImage"
              type="button"
              aria-label="Bild vergrößern"
              v-on:click="toggleImageMaximize"
              class="btn btn-sm btn-light position-absolute top-0 end-0 m-2"
            >
              <i class="bi bi-arrows-angle-expand" aria-hidden="true"></i>
            </button>
          </div>

          <!-- Fullscreen overlay -->
          <div
            v-if="isImageMaximized"
            class="viewer-image-fullscreen"
            v-on:click.self="toggleImageMaximize"
          >
            <button
              type="button"
              aria-label="Schließen"
              v-on:click="toggleImageMaximize"
              class="btn btn-light position-fixed top-0 end-0 m-3"
            >
              <i class="bi bi-x-lg" aria-hidden="true"></i>
            </button>
            <div class="viewer-image-fullscreen-inner">
              <nuxt-picture :src="imageSrc" :imgAttrs="{ class: 'viewer-image-fullscreen-img', alt: 'Vergrößertes Bild' }" />
            </div>
          </div>
        </div>
      </template>
      <template v-if="model.viewMode == 'dual' || model.viewMode == 'single'" #tei>
        <div class="viewer-text-content" ref="viewerRoot">
          <tei-element-convert v-if="teiBody" :tei-element="teiBody" :hook="elementFilter">
            <template #default="{ element }">
              <viewer-pb-element
                v-if="element.type === 'Element' && element.name === 'pb'"
                :show-image-icon="model.viewMode == 'dual'"
                :pb-element="element"
                :viewerRoot="viewerRoot"
                v-on:page-break-in-view="changeImage"
                v-on:image-icon-clicked="changeImage"
              />
              <ref-element
                v-else-if="element.type === 'Element' && (element.name === 'persName' || element.name === 'orgName')"
                :element="element"

              />

            </template>
          </tei-element-convert>
        </div>
      </template>
      <template v-if="model.viewMode == 'xml'" #xml>
        <div class="viewer-xml-content">
          <pre>{{ teiFileContent.data.value }}</pre>
        </div>
      </template>
    </rows>
  </div>

</template>

<script setup lang="ts">

import type {TEIElement, TEINode} from "~/api/tei.model";

const props = defineProps<{
  appUrl: string,
  mycoreId: string,
  derivateId: string,
  teiUrl: string
}>();

const {$tei} = useTei();

const viewerRoot = useTemplateRef("viewerRoot");

const model = reactive({
  viewMode: 'single' as 'single' | 'dual' | 'xml',
  currentImage: null as string|null,
});

const elementFilter = (el: TEINode) => {
  if(el.type === 'Element') {
    if(el.name === 'pb') {
      return true;
    }

    if(el.name === 'orgName') {
      return true;
    }

    if(el.name === 'persName') {
      return true;
    }
  }

  return false;
}

const teiFileContent = useAsyncData(`lod-viewer-tei-${props.mycoreId}-${props.derivateId}-${props.teiUrl}`, async () => {
  const response = await fetch(props.teiUrl);
  return await response.text();
});

const teiDocument = computed(() => {
  if (teiFileContent.data.value == null) {
    return null;
  }
  const parsedTei = $tei(teiFileContent.data.value);

  model.currentImage = parsedTei.find("pb").first().attr("facs") as string  || null;

  return parsedTei.get(0);
});

const teiBody = computed(() => {
  if (teiDocument.value == null) {
    return null;
  }
  return $tei(teiDocument.value).find("body").toArray()[0] || null;
});

// Neue Logik für Bildvergrößerung
const isImageMaximized = ref(false);

const imageSrc = computed(() => {
  if (!model.currentImage) return '';
  return `${props.appUrl}api/v2/objects/${props.mycoreId}/derivates/${props.derivateId}/contents/${model.currentImage.replace('#','')}`;
});

const toggleImageMaximize = () => {
  isImageMaximized.value = !isImageMaximized.value;
}

// ESC-Taste schließt die Vollbild-Ansicht
onMounted(() => {
  const onKey = (ev: KeyboardEvent) => {
    if (ev.key === 'Escape' && isImageMaximized.value) {
      isImageMaximized.value = false;
    }
  };
  window.addEventListener('keydown', onKey);
  onUnmounted(() => window.removeEventListener('keydown', onKey));
});


const changeViewMode = (mode: 'single' | 'dual' | 'xml') => {
  model.viewMode = mode;
}

const changeImage = (pbElement: TEIElement) => {
  if(pbElement.attributes.facs) {
    model.currentImage = pbElement.attributes.facs;
  }
}

</script>


<style scoped>

.viewer-content {
  margin-top: 1rem;
}

.viewer-nav {
  margin-top: 2rem;
}

.viewer-text-content, .viewer-xml-content {
  display: block;
  max-height: 500px;
  overflow-y: scroll;
  overflow-x: auto;
}


</style>

<style>

/* tei styles */

.tei-element[data-tei-name='noteGrp'] {
  display: none;
}


.pb-element {
  margin-bottom: 1rem;
  margin-top: 1rem;
}


.tei-element[data-tei-name="table"] {
  display: table;
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
  table-layout: fixed; /* equal column widths unless cells need more space */
  background: transparent;
  overflow: hidden;
  font-size: 0.95rem;
}

.tei-element[data-tei-name="row"] {
  display: table-row;
}

.tei-element[data-tei-name="cell"] {
  display: table-cell;
  /* border: 1px solid rgba(0,0,0,0.12); */
  padding: 0.5rem 0.75rem;
  vertical-align: top;
  word-wrap: break-word;
  hyphens: auto;
  white-space: normal;
  min-width: 6rem;
}


/* If the converter preserves xml:lang directly on the span use this; also support data-tei-attr-xml:lang form */
.tei-element[data-tei-name="cell"][xml\:lang="fr"],
.tei-element[data-tei-name="cell"][data-tei-attr-xml\:lang="fr"] {
  font-style: italic; /* French text often styled slightly differently */
}

/* Make tables responsive on small screens: stack rows as blocks */
@media (max-width: 720px) {
  .tei-element[data-tei-name="table"] {
    display: block;
  }
  .tei-element[data-tei-name="row"] {
    display: block;
    margin-bottom: 0.5rem;
  }
  .tei-element[data-tei-name="cell"] {
    display: block;
    width: 100% !important;
    box-sizing: border-box;
  }
}


[data-tei-attr-rendition~="#b"] {
  font-weight: bold;
}

[data-tei-attr-rendition~="#i"] {
  font-style: italic;
}

[data-tei-attr-rendition~="#sup"] {
  vertical-align: super;
  font-size: smaller;
}

[data-tei-attr-rendition~="#u"] {
  text-decoration: underline;
}

.tei-element[data-tei-name="del"] {
  text-decoration: line-through;
}

.tei-element[data-tei-name="add"] {
  text-decoration: underline;
}

.tei-element[data-tei-name="lb"] {
  display: block;
}




/* Viewer image styles */

.viewer-image {
  max-width: 100%;
  max-height: 500px; /* feste Max-Höhe hier statt Prozent */
  height: auto;
  width: auto;
  object-fit: contain; /* Bild bleibt im Verhältnis */
  display: block;
}


.viewer-image-content {
  max-height: 500px;
  overflow: auto; /* sorgt dafür, dass zu große Bilder scrollen */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* wrapper to position the maximize button */
.viewer-image-wrapper {
  position: relative;
  display: inline-block;
}

/* Entfernte eigene Button-Styles: wir nutzen Bootstrap-Utilities und Icons */

.viewer-image-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.85);
  display: flex;
  align-items: flex-start; /* allow scrolling and ensure tall images are not clipped */
  justify-content: center;
  z-index: 2000;
  padding: 1rem;
  box-sizing: border-box; /* damit Padding in der Berechnung berücksichtigt wird */
  overflow: auto; /* erlaubt Scrollen, falls nötig (kleine Bildschirme) */
}

/* Inner wrapper centers content vertically when there's enough space */
.viewer-image-fullscreen-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 2rem); /* sorgt dafür, dass das Bild zentriert wird, wenn es kleiner ist */
  width: 100%;
  box-sizing: border-box;
  padding-top: 1rem;
  padding-bottom: 1rem;
}

/* Bild im Fullscreen so skalieren, dass nichts abgeschnitten wird */
.viewer-image-fullscreen-img,
.viewer-image-fullscreen picture,
.viewer-image-fullscreen img,
.viewer-image-fullscreen picture img {
  /* großzügigeren Abstand lassen (Close-Button, Padding, evtl. Browser-UI) */
  max-width: calc(100vw - 4rem);
  max-height: calc(100vh - 8rem);
  width: auto !important;
  height: auto !important;
  object-fit: contain !important;
  display: block;
  margin: 0 auto;
}

/* Falls nuxt-picture damit ein <picture> enthält und dieses ein <img> als Kind hat, treffen wir dieses gezielt */
.viewer-image-fullscreen picture > img,
.viewer-image-fullscreen picture img {
  max-height: calc(100vh - 8rem) !important;
  max-width: calc(100vw - 4rem) !important;
  width: auto !important;
  height: auto !important;
}


</style>
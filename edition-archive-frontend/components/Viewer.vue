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
    <div class="viewer-content row">
      <div v-if="model.viewMode == 'dual'" class="viewer-col col-6">
        <div class="viewer-image-content">
          <iiif-image
            v-if="model.currentImage"
            :app-url="appUrl"
            :derivate-id="derivateId"
            :image-path="model.currentImage"
            :alt="$t('gpo.viewer.image.notAvailable')"
            :show-maximize-button="true"
          />
          <div v-else>
            <em>{{ $t('gpo.viewer.image.notAvailable') }}</em>
          </div>
        </div>
      </div>
      <div v-if="model.viewMode == 'dual' || model.viewMode == 'single'" :class="model.viewMode == 'dual' ? 'viewer-col col-6' : 'viewer-col col-12'">
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
            </template>
          </tei-element-convert>
        </div>
      </div>
      <div v-if="model.viewMode == 'xml'" class="viewer-col col-12">
        <div class="viewer-xml-content">
          <pre>{{ teiFileContent.data.value }}</pre>
        </div>
      </div>
    </div>
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
  }

  return false;
}

const resolveImagePath = (facsPath: string): string => {
  const cleanFacs = facsPath.replace(/^#/, '');
  try {
    const resolvedUrl = new URL(cleanFacs, props.teiUrl);
    const resolvedPath = resolvedUrl.pathname;
    const contentsMarker = '/contents/';
    const idx = resolvedPath.indexOf(contentsMarker);
    if (idx !== -1) {
      return resolvedPath.slice(idx + contentsMarker.length);
    }
  } catch {
    // fallback below
  }
  return cleanFacs;
};

const teiFileContent = useAsyncData(`lod-viewer-tei-${props.mycoreId}-${props.derivateId}-${props.teiUrl}`, async () => {
  const response = await fetch(props.teiUrl);
  return await response.text();
});

const teiDocument = computed(() => {
  if (teiFileContent.data.value == null) {
    return null;
  }
  const parsedTei = $tei(teiFileContent.data.value);

  const firstFacs = parsedTei.find("pb").first().attr("facs") as string;
  model.currentImage = firstFacs ? resolveImagePath(firstFacs) : null;

  return parsedTei.get(0);
});

const teiBody = computed(() => {
  if (teiDocument.value == null) {
    return null;
  }
  return $tei(teiDocument.value).find("body").toArray()[0] || null;
});


const changeViewMode = (mode: 'single' | 'dual' | 'xml') => {
  model.viewMode = mode;
}

const changeImage = (pbElement: TEIElement) => {
  if(pbElement.attributes.facs) {
    model.currentImage = resolveImagePath(pbElement.attributes.facs);
  }
}

</script>


<style scoped>

.viewer {
  height: 500px;
  display: flex;
  flex-direction: column;
}

.viewer-nav {
  flex-shrink: 0;
  margin-top: 2rem;
}

.viewer-content {
  flex: 1;
  min-height: 0;
  margin-top: 1rem;
  height: 0;
}

.viewer-col {
  height: 100%;
}

.viewer-text-content,
.viewer-xml-content {
  height: 100%;
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

.viewer-image-content {
  height: 100%;
  overflow: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}


</style>
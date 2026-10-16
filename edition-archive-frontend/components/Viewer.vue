<template>
  <div :class="['viewer', { 'viewer-fullscreen': model.fullscreen }]">
    <nav class="nav nav-tabs viewer-nav">
      <a :class="`nav-link${ model.viewMode == 'single' ? ' active':''}`"
         href="#"
         v-on:click.prevent="changeViewMode('single')"
      >{{ $t('metadata.viewer.viewMode.single') }}</a>
      <a :class="`nav-link${ model.viewMode == 'dual' ? ' active':''}`"
         href="#"
         v-on:click.prevent="changeViewMode('dual')"
      >{{ $t('metadata.viewer.viewMode.dual') }}</a>
      <a :class="`nav-link${ model.viewMode == 'xml' ? ' active':''}`"
         href="#"
         v-on:click.prevent="changeViewMode('xml')"
      >{{ $t('metadata.viewer.viewMode.xml') }}</a>
      <button
        type="button"
        class="btn btn-sm btn-light viewer-fullscreen-toggle"
        :aria-label="model.fullscreen ? $t('metadata.viewer.fullscreen.close') : $t('metadata.viewer.fullscreen.open')"
        :title="model.fullscreen ? $t('metadata.viewer.fullscreen.close') : $t('metadata.viewer.fullscreen.open')"
        v-on:click="toggleFullscreen"
      >
        <i :class="model.fullscreen ? 'bi bi-fullscreen-exit' : 'bi bi-fullscreen'" aria-hidden="true"></i>
      </button>
    </nav>
    <div class="viewer-content row">
      <div v-if="model.viewMode == 'dual'" class="viewer-col col-6">
        <div class="viewer-image-content" ref="imageContainerRef">
          <iiif-image
            v-if="model.currentImage"
            :app-url="appUrl"
            :derivate-id="derivateId"
            :image-path="model.currentImage"
            :alt="$t('gpo.viewer.image.notAvailable')"
            @loaded="onImageLoaded"
          />
          <div v-else class="viewer-image-placeholder">
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
                :is-active="isActivePb(element)"
                :footnote-reference="footnoteReferenceAfter(element)"
                v-on:page-break-in-view="changeImage"
                v-on:image-icon-clicked="changeImage"
              />
              <sup
                v-else-if="element.type === 'Element' && element.name === 'note' && element.attributes.place === 'foot' && !isFootnoteAfterPageBreak(element)"
                :id="footnoteReferenceId(element)"
                class="footnote-reference"
              >
                <a :href="`#${footnoteId(element)}`">{{ footnoteNumber(element) }}</a>
              </sup>
            </template>
          </tei-element-convert>

          <div v-if="footnotes.length > 0" class="footnotes-section">
            <div v-for="(note, index) in footnotes" :id="footnoteId(note)" :key="index" class="footnote-item">
              <a :href="`#${footnoteReferenceId(note)}`" class="footnote-number">{{ index + 1 }}</a> <tei-element-convert :tei-element="note" />
            </div>
          </div>
        </div>
      </div>
      <div v-if="model.viewMode == 'xml'" class="viewer-col col-12">
        <div class="viewer-xml-content">
          <pre>{{ teiBodyXml }}</pre>
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

const {$tei, serializeFirstElement} = useTei();

const viewerRoot = useTemplateRef("viewerRoot");
const imageContainerRef = useTemplateRef<HTMLDivElement>("imageContainerRef");

const model = reactive({
  viewMode: 'single' as 'single' | 'dual' | 'xml',
  currentImage: null as string|null,
  fullscreen: false,
});

let shouldResetScroll = false;

const elementFilter = (el: TEINode) => {
  if(el.type === 'Element') {
    if(el.name === 'pb') {
      return true;
    }
    if(el.name === 'note' && el.attributes.place === 'foot') {
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
  shouldResetScroll = true;

  return parsedTei.get(0);
});

const teiBody = computed(() => {
  if (teiDocument.value == null) {
    return null;
  }
  return $tei(teiDocument.value).find("body").toArray()[0] || null;
});

const footnotes = computed(() => {
  if (teiDocument.value == null) {
    return [];
  }

  return $tei(teiDocument.value)
    .find((element) => element.name === 'note' && element.attributes.place === 'foot')
    .toArray()
    .filter((note): note is TEIElement => note.type === 'Element');
});

const footnoteNumber = (note: TEIElement): number => {
  return footnotes.value.findIndex((footnote) => footnote === note) + 1;
};

const footnoteId = (note: TEIElement): string => {
  return `footnote-${footnoteNumber(note)}`;
};

const footnoteReferenceId = (note: TEIElement): string => {
  return `footnote-ref-${footnoteNumber(note)}`;
};

const footnoteReferenceAfter = (pb: TEIElement) => {
  const parent = pb.parent;
  if (!parent) return null;
  const pbIndex = parent.children.indexOf(pb);
  const next = parent.children[pbIndex + 1];
  if (next?.type !== 'Element' || next.name !== 'note' || next.attributes.place !== 'foot') {
    return null;
  }
  return {
    id: footnoteReferenceId(next),
    targetId: footnoteId(next),
    number: footnoteNumber(next),
  };
};

const isFootnoteAfterPageBreak = (note: TEIElement): boolean => {
  const parent = note.parent;
  if (!parent) return false;
  const noteIndex = parent.children.indexOf(note);
  const previous = parent.children[noteIndex - 1];
  return previous?.type === 'Element' && previous.name === 'pb';
};

const teiBodyXml = computed(() => {
  const raw = teiFileContent.data.value;
  if (!raw) return '';
  return serializeFirstElement(raw, 'body') ?? raw;
});


const changeViewMode = (mode: 'single' | 'dual' | 'xml') => {
  model.viewMode = mode;
}

const toggleFullscreen = () => {
  model.fullscreen = !model.fullscreen;
}

const changeImage = (pbElement: TEIElement) => {
  if (!pbElement.attributes.facs) return;
  const newPath = resolveImagePath(pbElement.attributes.facs);
  if (newPath === model.currentImage) return;
  shouldResetScroll = true;
  model.currentImage = newPath;
}

const isActivePb = (el: TEIElement): boolean => {
  if (!model.currentImage) return false;
  const facs = el.attributes.facs;
  if (!facs) return false;
  return resolveImagePath(facs) === model.currentImage;
}

const onImageLoaded = async () => {
  if (!shouldResetScroll) return;
  shouldResetScroll = false;
  await nextTick();
  const container = imageContainerRef.value;
  if (!container) return;
  container.scrollTop = 0;
}

const onKeyDown = (ev: KeyboardEvent) => {
  if (ev.key === 'Escape' && model.fullscreen) {
    model.fullscreen = false;
  }
}

let previousBodyOverflow = '';
watch(() => model.fullscreen, (fullscreen) => {
  if (typeof document === 'undefined') return;
  if (fullscreen) {
    previousBodyOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = previousBodyOverflow;
  }
});

onMounted(() => {
  window.addEventListener('keydown', onKeyDown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onKeyDown);
  if (typeof document !== 'undefined' && model.fullscreen) {
    document.body.style.overflow = previousBodyOverflow;
  }
});

</script>


<style scoped>

.viewer {
  height: 500px;
  display: flex;
  flex-direction: column;
}

.viewer-fullscreen {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  background: #fff;
  z-index: 2000;
  padding: 0.5rem 1rem 1rem 1rem;
  box-sizing: border-box;
}

.viewer-nav {
  flex-shrink: 0;
  margin-top: 2rem;
  align-items: center;
}

.viewer-fullscreen .viewer-nav {
  margin-top: 0;
}

.viewer-fullscreen-toggle {
  margin-left: auto;
}

.viewer-content {
  flex: 1;
  min-height: 0;
  margin-top: 1rem;
  height: 0;
}

.viewer-col {
  height: 100%;
  min-height: 0;
}

.viewer-image-content {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

.viewer-image-placeholder {
  padding: 2rem;
  text-align: center;
  color: #666;
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

[data-tei-attr-rendition~="#s"] {
  text-decoration: line-through;
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

.tei-element[data-tei-name="p"], .tei-element[data-tei-name="title"], .tei-element[data-tei-name="note"] {
  display: block;
  min-height: 1em;
}

.tei-element[data-tei-name="head"] {
  display: block;
  font-weight: bold;
  padding-top: 1em;
  padding-bottom: 1em;
}


/* letter structure (opener / closer) — mirror the LeafWriter layout */
.tei-element[data-tei-name="opener"],
.tei-element[data-tei-name="closer"] {
  display: block;
}

.tei-element[data-tei-name="salute"],
.tei-element[data-tei-name="dateline"],
.tei-element[data-tei-name="signed"] {
  display: block;
  padding: 0.25em 0.5em;
}

/* the closing part (date, valediction, signature) is right-aligned;
   text-align is inherited by the contained dateline / salute / signed */
.tei-element[data-tei-name="closer"] {
  text-align: right;
}

.tei-element[data-tei-name="signed"] {
  text-align: right;
}

/* a signature persName (@type/@rendition="signature") that is not wrapped
   in <signed> still has to render as a right-aligned block of its own */
.ref-element.tei-signature {
  display: block;
  text-align: right;
  padding: 0.25em 0.5em;
}

/* footnote styles */
.tei-element[data-tei-name="note"][data-tei-attr-place="foot"] {
  display: block;
  font-size: 0.85em;
  line-height: 1.4;
  margin-top: 0.5em;
}

.footnotes-section {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #ccc;
}

.footnotes-section h3 {
  font-size: 1em;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

.footnote-item {
  margin-bottom: 0.5em;
}

.footnote-item > .tei-element[data-tei-name="note"][data-tei-attr-place="foot"] {
  display: inline;
  margin-top: 0;
}

.footnote-reference {
  display: inline;
  margin: 0;
  padding: 0;
}

</style>

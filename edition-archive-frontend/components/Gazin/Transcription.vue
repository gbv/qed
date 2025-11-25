<template>
  <div>
    <template v-if="body">
      <div class="transcription">
        <tei-element-convert :tei-element="body" :hook="hook">
          <template #default="{ element }">
            <tei-editorial-note v-if="isEditorialNoteRef(element)" :note="element" />
            <!-- v-if isEdtitorialNoteNote(element) ignore the element-->
          </template>
        </tei-element-convert>
      </div>
    </template>
    <div v-else class="text-center">
      <span class="spinner-border" role="status" aria-hidden="true"></span>
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script setup lang="ts">

import {type TEINode, type TEIComment, type TEIText, type TEIElement} from "~/api/tei.model";

const props = defineProps<{ teiUrl: string }>();

const {$tei} = useTei();


const teiStr = useAsyncData(`transcription-${props.teiUrl}`, async () => {
  const response = await fetch(props.teiUrl);
  return await response.text();
});

const body = computed(() => {
  if (teiStr.data.value == null) {
    return null;
  }
  const tei = $tei(teiStr.data.value)
  return tei.find("body").toArray()[0] || null;
});

const isEditorialNoteRef = (el: TEIElement | TEIText | TEIComment) => {
  return el.type == "Element" && el.name == "ref" && el.attributes.type == "editorialNote";
}

const isEdtitorialNoteNote = (el: TEINode) => {
  return el.type == "Element" && el.name == "note" && el.attributes.type == "editorial";
}

const hook = (el: TEINode) => {
  if(isEditorialNoteRef(el)) {
    return true;
  }
  if(isEdtitorialNoteNote(el)) {
    return true;
  }

  return false;
}

</script>

<style>

:root {
  --qed-counter-width: 3.15rem; /* Platz für die Nummernspalte */
}

.tei-element[data-tei-attr-type="song"] {
  counter-reset: line 0;
}

.tei-element[data-tei-name="head"] {
  display: block;
  background-color: #f1f1f1;
  padding-left: var(--qed-counter-width);
}

.tei-element[data-tei-name="lg"] {
  display: block;
  margin-bottom: 3rem;
}

.tei-element[data-tei-name="l"] {
  counter-increment: line;
  display: block;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.2rem;
  margin-bottom: 0.2rem;
}

.tei-element[data-tei-name="l"] {
  position: relative;
  padding-left: var(--qed-counter-width);
  counter-increment: line;
  display: block;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.2rem;
  margin-bottom: 0.2rem;
  box-sizing: border-box;
}

.tei-element[data-tei-name="l"]::before {
  content: counter(line);
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--qed-counter-width);
  display: flex;
  align-items: center;    /* vertikal zentriert über die volle Höhe */
  justify-content: flex-end;
  padding-right: 2rem;
  height: auto;
  font-size: 0.7rem;
}

[data-tei-attr-rendition="#b"] {
  font-weight: bold;
}

[data-tei-attr-rendition="#i"] {
  font-style: italic;
}
</style>
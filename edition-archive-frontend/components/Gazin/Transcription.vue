<template>
  <div>
    <template v-if="body">
      <div class="transcription">
        <TeiElementConvert :tei-element="body" :hook="hook">
          <template #default="{ element }">
            <editorial-note v-if="isEditorialNoteRef(element)" :note="element" />
            <!-- v-if isEdtitorialNoteNote(element) ignore the element-->
          </template>
        </TeiElementConvert>
      </div>
    </template>
    <div v-else class="text-center">
      <span class="spinner-border" role="status" aria-hidden="true"></span>
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script setup lang="ts">

import $tei, {type TEINode, type TEIComment, type TEIText, type TEIElement} from "~/api/tei";
import TeiElementConvert from "~/components/Gazin/TeiElementConvert.vue";
import EditorialNote from "~/components/Gazin/EditorialNote.vue";

const props = defineProps<{ teiUrl: string }>();

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

.tei-element[data-tei-attr-type="song"] {
  counter-reset: line 0;
}

.tei-element[data-tei-name="head"] {
  display: block;
  background-color: #f1f1f1;
}

.tei-element[data-tei-name="head"]::before {
  content: '#';
  padding-right: 2em;
}

.tei-element[data-tei-name="lg"] {
  display: block;
  margin-bottom: 1em;
}

.tei-element[data-tei-name="l"] {
  counter-increment: line;
  display: block;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.2em;
  margin-bottom: 0.2em;
}

.tei-element[data-tei-name="l"]::before {
  content: counter(line);
  padding-right: 2em;
}

[data-tei-attr-rendition="#b"] {
  font-weight: bold;
}
</style>
<template>
  <span class="tei-element" :data-tei-name="tagName" v-if="elementType=='Element'"
        v-bind="attributes">
    <template v-for="child in children">
      <ElementConvert
        v-if="!defaultHook(child) && (props.hook == undefined || !props.hook(child) || $slots.default)"
        :tei-element="child"
        :hook="props.hook"
      >
        <template #default="slotProps" v-if="$slots.default">
          <slot name="default" v-bind="slotProps"></slot>
        </template>
      </ElementConvert>
      <template v-else-if="$slots.default && props.hook != undefined && props.hook(child)">
        <slot name="default" :element="child" />
      </template>
      <template v-else-if="child.type == 'Element' && defaultHook(child)">
        <tei-ref-element
          v-if="(child.name === 'persName' || child.name === 'orgName' || child.name === 'placeName')"
          :element="child"
        />
        <tei-editorial-note v-if="isEditorialNoteRef(child)" :note="child" />
      </template>
    </template>
  </span>
  <span class="tei-text" v-else-if="elementType=='Text'">
    {{ text }}
  </span>
</template>

<script setup lang="ts">
import {type TEIComment, type TEIElement, type TEINode, type TEIText} from "~/api/tei.model";


const isEditorialNoteRef = (el: TEIElement | TEIText | TEIComment) => {
  return el.type == "Element" && el.name == "ref" && el.attributes.type == "editorialNote";
}

const isEdtitorialNoteNote = (el: TEINode) => {
  return el.type == "Element" && el.name == "note" && el.attributes.type == "editorial";
}

const defaultHook = (el: TEINode) => {
  if (isEditorialNoteRef(el)) {
    return true;
  }
  if (isEdtitorialNoteNote(el)) {
    return true;
  }
  if (el.type === "Element" && (el.name === "persName" || el.name === "orgName" || el.name === "placeName")) {
    return true;
  }

  return false;
}

const props = defineProps<{
  teiElement: TEINode,
  hook?: (el: TEINode) => boolean;
}>();

const slots = defineSlots<{
  [key: string]: { element: TEIElement | TEIText | TEIComment }
}>();

const elementType = computed(() => {
  return props.teiElement.type;
});

const children = computed(() => {
  if (props.teiElement.type === "Element") {
    return props.teiElement.children;
  } else {
    return [];
  }
});

const text = computed(() => {
  if (props.teiElement.type === "Text") {
    return props.teiElement.text;
  } else {
    return "";
  }
});

const tagName = computed(() => {
  if (props.teiElement.type === "Element") {
    return props.teiElement.name;
  } else {
    return "";
  }
});

const attributes = computed(() => {
  if (props.teiElement.type === "Element") {
    const attributes: Record<string, string> = {};
    for (const [key, value] of Object.entries(props.teiElement.attributes)) {
      attributes["data-tei-attr-" + key] = value;
    }
    return attributes;
  } else {
    return {};
  }
});

</script>


<style scoped>

</style>
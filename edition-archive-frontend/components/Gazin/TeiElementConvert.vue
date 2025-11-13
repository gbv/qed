<template>
  <span class="tei-element" :data-tei-name="tagName" v-if="elementType=='Element'"
        v-bind="attributes">
    <template v-for="child in children">
      <TeiElementConvert
        v-if="props.hook == undefined || !props.hook(child)"
        :tei-element="child"
        :hook="props.hook"
      >
        <template #default="slotProps" v-if="$slots.default">
          <slot name="default" v-bind="slotProps"></slot>
        </template>
      </TeiElementConvert>
      <template v-else-if="$slots.default">
        <slot name="default" :element="child" />
      </template>
    </template>
  </span>
  <span class="tei-text" v-else-if="elementType=='Text'">
    {{ text }}
  </span>
</template>

<script setup lang="ts">
import {type TEIComment, type TEIElement, type TEINode, type TEIText} from "~/api/tei.model";


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
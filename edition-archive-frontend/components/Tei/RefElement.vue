<template>
  <div class="ref-element popout-wrapper position-relative d-inline">
    <component :is="textTag" v-bind="textAttrs" v-on:click.prevent="model.show ? hide():show()">
      <i v-if="elementName === 'persName'" class="bi bi-person"></i>
      <i v-else-if="elementName === 'orgName'" class="bi bi-bank"></i>
      <i v-else-if="elementName === 'placeName'" class="bi bi-geo-alt"></i>{{ contentText }}
    </component>

    <div v-if="model.show && hasRefAttribute && isCompatibleRef" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hide()"><i class="bi bi-x-circle"></i></a>
      <tei-ref-resolver :v-if="hasRefAttribute" :ref-attribute="props.element.attributes.ref" />
    </div>

  </div>
</template>
<script setup lang="ts">

import type {TEIElement} from "~/api/tei.model";

const props = defineProps<{
  element: TEIElement
}>();

const model = reactive({
  show: false as boolean,
});

const textTag = computed(() => {
  if(hasRefAttribute.value) {
    return "a";
  } else {
    return "span";
  }
});

const textAttrs = computed(() => {
  if(hasRefAttribute.value) {
    return {
      href: "#ref",
      class: "ref-element ref-element-link",
    };
  } else {
    return {
      class: "ref-element",
    };
  }
});

const {$tei} = useTei();

const elementName = computed(() => {
  return props.element.name;
});

const contentText = computed(() => {
  return $tei(props.element).text();
});

const hasRefAttribute = computed(() => {
  return 'ref' in props.element.attributes;
});

const isCompatibleRef = computed(() => {
  const ref = props.element.attributes.ref;
  if (!ref) {
    return false;
  }

  return ref.startsWith('http://uri.gbv.de/terminology/') || ref.startsWith('https://uri.gbv.de/terminology/')
  || ref.startsWith('https://sws.geonames.org/') || ref.startsWith('https://geonames.org/');
});

const hide = () => {
  model.show = false;
};

const show = () => {
  model.show = true;
};

</script>


<style scoped>

i {
  margin-right: 3px;
}

/* prevents unwanted line breaks */
.bi::before, [class^="bi-"]::before, [class*=" bi-"]::before {
  display: inline;
}

.editorial-note:hover {
  background-color: rgb(225, 225, 225);
}


.ref-element:hover {
  background-color: rgb(225, 225, 225);
}

.popout {
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px;
  padding: 1rem;
  margin: 1rem;
  min-height: 5rem;
}
</style>
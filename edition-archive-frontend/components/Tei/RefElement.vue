<template>
  <span class="ref-element popout-wrapper position-relative d-inline">
    <a class="ref-element" href="#ref" v-if="hasRefAttribute" v-on:click.prevent="model.show ? hide():show()">
      <i v-if="elementName === 'persName'" class="bi bi-person"></i>
      <i v-else-if="elementName === 'orgName'" class="bi bi-bank"></i>
      <i v-else-if="elementName === 'placeName'" class="bi bi-geo-alt"></i>{{ contentText }}
    </a>

    <div v-if="model.show && isCompatibleRef" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hide()"><i class="bi bi-x-circle"></i></a>
      <lazy-tei-ref-resolver :v-if="hasRefAttribute" :ref-attribute="props.element.attributes.ref" />
    </div>

  </span>
</template>
<script setup lang="ts">

import type {TEIElement} from "~/api/tei.model";

const props = defineProps<{
  element: TEIElement
}>();

const model = reactive({
  show: false as boolean,
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

  return ref.startsWith('http://uri.gbv.de/terminology/') || ref.startsWith('https://uri.gbv.de/terminology/');
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
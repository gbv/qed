<template>
  <div>
    <JSKOSGBVRefResolver v-if="isGBVTerminology(refAttribute)" :ref-attribute="props.refAttribute" />
    <GEONamesRefResolver v-if="isGeoNames(refAttribute)" :ref-attribute="props.refAttribute" />
  </div>
</template>

<script setup lang="ts">


import JSKOSGBVRefResolver from "~/components/Tei/JSKOSGBVRefResolver.vue";
import GEONamesRefResolver from "~/components/Tei/GEONamesRefResolver.vue";

const isGBVTerminology = (ref: string) => {
  return ref.startsWith("http://uri.gbv.de/terminology/");
}

const isGeoNames = (ref: string) => {
  return ref.startsWith("https://sws.geonames.org/") || ref.startsWith("https://geonames.org/")
    || ref.startsWith("https://www.geonames.org/");
};

const props = defineProps<{
  refAttribute: string,
}>();

</script>

<style scoped>

</style>
<template>
  <span class="place" v-if="$props.place">
    <GalliaPontificiaOnlinePlace :place-id="getAttribute($props.place, 'key')?.value">
      <GalliaPontificiaOnlineRegestMixedContent v-for="c in $props.place.content" :content="c"/>
    </GalliaPontificiaOnlinePlace>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "~/api/XMLApi"

const route = useRoute()
const props = defineProps(["place"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>
/* prevents unwanted line breaks */
.bi::before, [class^="bi-"]::before, [class*=" bi-"]::before {
  display: inline;
}

.place:hover {
  background-color: rgb(225, 225, 225);
}
</style>
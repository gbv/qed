<template>
  <span class="place" v-if="$props.place">
     <nuxt-link :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/ort?ortObj=${ getAttribute($props.place, 'key')?.value }`">
       {{ flattenElement($props.place) }} <span class="bi bi-geo-alt"> </span>
     </nuxt-link>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["place"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>
.place:hover {
  background-color: rgb(225, 225, 225);

}
</style>
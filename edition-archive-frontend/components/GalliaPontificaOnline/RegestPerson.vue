<template>
  <span v-if="$props.person">
     <nuxt-link :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/person?personObj=${ getAttribute($props.person, 'key')?.value }`">
       {{ flattenElement($props.person) }} <span class="bi bi-person"> </span>
     </nuxt-link>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["person"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>

</style>
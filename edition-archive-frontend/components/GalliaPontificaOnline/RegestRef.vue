<template>
  <nuxt-link v-if="getAttribute($props.refE,'type', 'internal') &&
                  getAttribute($props.refE, 'n', 'regesten')?.value"
             :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/${getAttribute($props.refE, 'target')?.value}`">
    {{ flattenElement($props.refE) }}
  </nuxt-link>

</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["refE"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>

</style>
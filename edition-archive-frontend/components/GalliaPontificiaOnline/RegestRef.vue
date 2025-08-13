<template>
  <nuxt-link v-if="getAttribute($props.refE,'type', 'internal') &&
                  getAttribute($props.refE, 'n', 'regesten')?.value"
             :href="`/gallia-pontificia-online/regesten/${route.params.regesten}/regest/${getAttribute($props.refE, 'target')?.value}`">
    {{ flattenElement($props.refE) }}
  </nuxt-link>
  <a v-else-if="getAttribute($props.refE,'type', 'external')"
     :href="getAttribute($props.refE, 'target')?.value">
    {{ flattenElement($props.refE) }}
  </a>
  <span v-else>
    {{ flattenElement($props.refE) }}
  </span>

</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "~/api/XMLApi"

const route = useRoute()
const props = defineProps(["refE"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>

</style>
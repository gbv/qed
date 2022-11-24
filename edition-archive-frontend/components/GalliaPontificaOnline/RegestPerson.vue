<template>
  <span class="person" v-if="$props.person">
     <GalliaPontificaOnlineRegestMixedContent v-for="c in $props.person.content" :content="c"/>
     <nuxt-link :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/person?personObj=${ getAttribute($props.person, 'key')?.value }`">
        <span class="bi bi-person"> </span>
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
.person:hover {
  background-color: rgb(225, 225, 225);

}
</style>
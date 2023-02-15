<template>
  <span class="msidentifier" v-if="$props.msidentifier">
     <GalliaPontificaOnlineRegestMixedContent v-for="c in $props.msidentifier.content" :content="c"/>
     <nuxt-link class="icon-link" :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/handschriften?handschriftenKey=${ getAttribute($props.msidentifier, 'n')?.value }`">
        <span class="bi bi-book"> </span>
     </nuxt-link>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["msidentifier"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>
/* prevents unwanted line breaks */
.bi::before, [class^="bi-"]::before, [class*=" bi-"]::before {
  display: inline;
}

.msidentifier:hover {
  background-color: rgb(225, 225, 225);
}

a {
  margin-left: 0.25em;
}
</style>
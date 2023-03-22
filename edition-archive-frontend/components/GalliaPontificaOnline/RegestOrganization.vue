<template>
  <span class="organization" v-if="$props.organization">
    <GalliaPontificaOnlineOrganization :organization-id="getAttribute($props.organization, 'key')?.value">
       <GalliaPontificaOnlineRegestMixedContent v-for="c in $props.organization.content" :content="c"/>
    </GalliaPontificaOnlineOrganization>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["organization"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>
/* prevents unwanted line breaks */
.bi::before, [class^="bi-"]::before, [class*=" bi-"]::before {
  display: inline;
}

.person:hover {
  background-color: rgb(225, 225, 225);
}
</style>
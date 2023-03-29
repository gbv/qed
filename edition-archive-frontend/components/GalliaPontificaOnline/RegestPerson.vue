<template>
  <span class="person" v-if="$props.person">
    <GalliaPontificaOnlinePerson :person-id="getAttribute($props.person, 'key')?.value">
       <GalliaPontificaOnlineRegestMixedContent v-for="c in $props.person.content" :content="c"/>
    </GalliaPontificaOnlinePerson>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["person"])

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
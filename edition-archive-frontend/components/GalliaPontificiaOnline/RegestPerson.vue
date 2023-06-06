<template>
  <span class="person" v-if="$props.person">
    <GalliaPontificiaOnlinePerson :person-id="getAttribute($props.person, 'key')?.value">
       <GalliaPontificiaOnlineRegestMixedContent v-for="c in $props.person.content" :content="c"/>
    </GalliaPontificiaOnlinePerson>
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
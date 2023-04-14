<template>
  <span class="msidentifier" v-if="$props.msidentifier">
    <GalliaPontificaOnlineMsIdentifier :handschriften-key="identifier">
    </GalliaPontificaOnlineMsIdentifier>
    <GalliaPontificaOnlineRegestMixedContent v-for="c in $props.msidentifier.content" :content="c"/>
</span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["msidentifier"])

const {$solrURL, $backendURL} = useNuxtApp();

const identifier = computed(() => {
  return getAttribute(props.msidentifier, 'n')?.value || ''
});

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
  margin-right: 0.25em;
}
</style>
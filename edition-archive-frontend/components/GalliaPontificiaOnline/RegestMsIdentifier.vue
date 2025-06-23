<template>
  <span class="msidentifier" v-if="$props.msidentifier">
    <GalliaPontificiaOnlineMsIdentifier :handschriften-key="identifier">
      <GalliaPontificiaOnlineRegestMixedContent v-for="c in $props.msidentifier.content" :content="c"/>
    </GalliaPontificiaOnlineMsIdentifier>
</span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "~/api/XMLApi"

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
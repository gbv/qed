<template>
  <div>
    <a class="mb-1" :href="props.refAttribute" target="_blank" rel="noopener noreferrer">
      {{ props.refAttribute }}
    </a>
    <div v-if="model.resolvedSkos">

      <j-skos-lang-map-display
        v-if="model.resolvedSkos.prefLabel"
        :lang-map="model.resolvedSkos.prefLabel"
        translation-key="metadata.skos.prefLabel"
      />

      <j-skos-lang-map-display
        v-if="model.resolvedSkos.altLabel"
        :lang-map="model.resolvedSkos.altLabel"
        translation-key="metadata.skos.altLabel"
      />

      <j-skos-lang-map-display
        v-if="model.resolvedSkos.definition"
        :lang-map="model.resolvedSkos.definition"
        translation-key="metadata.skos.definition"
      />

    </div>
    <div class="text-center" v-else-if="model.resolvedSkos == null">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">


import type {JSKOSEntity} from "~/api/jskos";

const props = defineProps<{
  refAttribute: string,
}>();

const model = reactive({
  resolvedSkos: null as JSKOSEntity | null,
  isSkos: false as boolean,
});

const resolveGBVTerm = async (ref: string) => {
  const response = await fetch(`https://api.dante.gbv.de/data?uri=${ref}&properties=*`);
  if (!response.ok) {
    return null;
  }
  return (await response.json() as JSKOSEntity[])[0] as JSKOSEntity;
};

watch(() => props.refAttribute, async (newRef) => {
  model.resolvedSkos = null;
  model.resolvedSkos = await resolveGBVTerm(newRef);
}, {immediate: true});


</script>

<style scoped>

</style>
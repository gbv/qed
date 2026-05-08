<template>
  <div>
    <div v-if="model.geonamesEntry">
      <ref-label-lang-value-display v-if="model.geonamesEntry.name" label-translation-key="metadata.geonames.name" :value="model.geonamesEntry.name" />

      <ref-label-lang-value-display v-if="model.geonamesEntry.lat && model.geonamesEntry.lng" label-translation-key="metadata.geonames.coordinates">
        <template #default>
          <span>{{ `${model.geonamesEntry.lat}, ${model.geonamesEntry.lng}` }}</span>
          <Collapse :labelKey="'metadata.subject.showMap'" :labelKeyOpen="'metadata.subject.hideMap'">
            <client-only>
              <map-coordinates :height="200" :coordinates="`${model.geonamesEntry.lng} ${model.geonamesEntry.lat}`" />
            </client-only>
          </Collapse>
        </template>
      </ref-label-lang-value-display>

    </div>
  </div>
</template>


<script setup lang="ts">

import type {GeoNamesEntry} from "~/api/GeoNames";
import RefLabelLangValueDisplay from "~/components/Tei/RefLabelLangValueDisplay.vue";
import Collapse from "~/components/Ui/Collapse.vue";

const props = defineProps<{
  refAttribute: string,
}>();

const {public: {geonamesUsername}} = useRuntimeConfig()

const model = reactive({
  geonamesEntry: null as GeoNamesEntry | null,
});

const resolveGeoNames = async (ref: string) => {
  const geonameIdMatch = ref.match(/https:\/\/sws\.geonames\.org\/(\d+)/);
  const geonameIdMatchAlt = ref.match(/https:\/\/geonames\.org\/(\d+)/);
  const geonameIdMatchAlt2 = ref.match(/https:\/\/www\.geonames\.org\/(\d+)/);


  let geonameId;
  if(geonameIdMatch != null && geonameIdMatch.length > 1) {
    geonameId = geonameIdMatch[1];
  } else if(geonameIdMatchAlt != null && geonameIdMatchAlt.length > 1) {
    geonameId = geonameIdMatchAlt[1];
  } else if(geonameIdMatchAlt2 != null && geonameIdMatchAlt2.length > 1) {
    geonameId = geonameIdMatchAlt2[1];
  } else {
    console.warn("GeoNames ref does not contain a valid geonameId:", ref);
    return null;
  }
  let input = `https://www.geonames.org/getJSON?geonameId=${geonameId}&username=${geonamesUsername}`;
  const response = await fetch(input);
  if (!response.ok) {
    return null;
  }
  return await response.json() as GeoNamesEntry;
};


watch(() => props.refAttribute, async (newRef) => {
  model.geonamesEntry = null;
  model.geonamesEntry = await resolveGeoNames(newRef);
}, {immediate: true});

model.geonamesEntry = await resolveGeoNames(props.refAttribute);

</script>

<style scoped>

</style>
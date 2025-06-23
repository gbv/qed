<template>

  <ol-map     :loadTilesWhileAnimating="true"
                       :loadTilesWhileInteracting="true"
                       style="height:600px">

    <ol-view
      ref="view"
      :center="transform([props.centerX, props.centerY], 'EPSG:4326', 'EPSG:3857')"
      :rotation="model.rotation"
      :zoom="model.zoom"
      :projection="model.projection"
    />

    <ol-tile-layer>
      <ol-source-osm />
    </ol-tile-layer>

    <ol-vector-layer>
      <ol-source-vector :features="convertedSolrDocument">
      </ol-source-vector>
      <ol-style>
        <ol-style-icon :src="markerIcon" :scale="0.5"></ol-style-icon>
      </ol-style>
    </ol-vector-layer>

    <ol-interaction-select
    @select="selected"
    >
      <ol-style>
        <ol-style-icon :src="markerIconSelected" :scale="1"></ol-style-icon>
      </ol-style>
    </ol-interaction-select>

  </ol-map>
</template>

<script setup lang="ts">


import {transform} from 'ol/proj';
import  {WKT} from 'ol/format';

import markerIcon from "assets/map_pin_red.svg";
import markerIconSelected from "assets/map_pin_dark_red.svg";
import Feature from "ol/Feature";
import {Select} from "ol/interaction";
import type {SelectEvent} from "ol/interaction/Select";

const emit = defineEmits({
  'objectSelected': (feature: any|null) => true
});

const props = defineProps<{
  // hier muss das solr response als json rein welches, die dokumente mit dem Feld common.mods.coordinates mit WKT enthält
  solrResponse: any,
  centerX: number,
  centerY: number
}>()

const wktField = "common.mods.coordinates";
const wkt = new WKT();

const convertedSolrDocument = computed(() => {
  if(!props.solrResponse || props.solrResponse.response === undefined || props.solrResponse.response.numFound == 0) {
    return [];

  }
  const convertedFeatures = [];
  for(const solrDoc of props.solrResponse.response.docs) {
    if(!solrDoc["common.mods.coordinates"]) {
      continue;
    }

    for (const coord of solrDoc[wktField]) {
      const geometry = wkt.readGeometry(coord, {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857'
      });

      if(!geometry) {
        continue;
      }

      const feature = new Feature({
        geometry,
        properties: solrDoc
      });

      convertedFeatures.push(feature);
    }

  }
  return convertedFeatures;
});



const model = reactive({
  projection: "EPSG:3857",
  zoom: 5,
  rotation: 0
});

const selected = (selectEvent: SelectEvent) => {
  if(selectEvent.deselected !== undefined) {
    emit('objectSelected', null);
  }
  if(selectEvent.type=== 'select') {
    if(selectEvent.selected !== undefined && selectEvent.selected.length > 0) {
      emit('objectSelected', selectEvent.selected[0].getProperties().properties);
      return;
    }
  }


};



</script>

<style scoped>
@import 'vue3-openlayers/dist/vue3-openlayers.css';

</style>
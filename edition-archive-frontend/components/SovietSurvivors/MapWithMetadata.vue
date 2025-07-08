<template>

  <ol-map     :loadTilesWhileAnimating="true"
                       :loadTilesWhileInteracting="true"
                       style="height:600px">

    <ol-view
      ref="view"
      :center="model.position"
      :rotation="model.rotation"
      :zoom="model.zoom"
      :projection="model.projection"
    />

    <ol-tile-layer>
      <ol-source-osm />
    </ol-tile-layer>

    <ol-vector-layer>
      <ol-source-cluster :distance="20">
        <ol-source-vector :features="convertedSolrDocument">
        </ol-source-vector>
      </ol-source-cluster>

      <ol-style :overrideStyleFunction="styleCluster"/>

    </ol-vector-layer>

    <ol-interaction-select
      :spiral="false"
      :animate="false"
      :autoclose="true"
      @select="selected">


      <ol-style :overrideStyleFunction="styleSelectedCluster" />

    </ol-interaction-select>

    <ol-overlay
      v-if="model.selectedFeatures.length > 0"
      :position="[model.overlayPositionX, model.overlayPositionY, 500, 500]"
      :autoPan="true"
    >
      <div class="overlay-content">
       <slot name="metadata" :solrdocs="selectedProperties"></slot>
      </div>
    </ol-overlay>


  </ol-map>
</template>

<script setup lang="ts">


import {transform} from 'ol/proj';
import  {WKT} from 'ol/format';

import markerIcon from "assets/map_pin_red.svg";
import markerIconSelected from "assets/map_pin_dark_red.svg";
import Feature, {type FeatureLike} from "ol/Feature";
import {Select} from "ol/interaction";
import type {SelectEvent} from "ol/interaction/Select";
import {Style, Circle, Stroke, Fill, Text} from "ol/style";


import {Cluster} from 'ol/source';
import type GeometryCollection from "ol/geom/GeometryCollection";
import {Point} from "ol/geom";


const props = defineProps<{
  // hier muss das solr response als json rein welches, die dokumente mit dem Feld common.mods.coordinates mit WKT enthält
  solrResponse: any,
  centerX: number,
  centerY: number,
}>();

const slots = defineSlots<{
    metadata: (scope: { solrdocs: any[] }) => any
}>()

const wktField = "common.mods.coordinates";
const wkt = new WKT();

const selectedProperties = computed(() => {
  return model.selectedFeatures.map((feature: Feature) => {
    return feature.getProperties().properties;
  });
})


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
      let geometry = wkt.readGeometry(coord, {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857'
      });

      if(!geometry) {
        continue;
      }

      if (geometry.getType() === "GeometryCollection") {
        const geometryCollection = geometry as GeometryCollection;
        if (geometryCollection.getGeometries().length > 0) {
          geometry = geometryCollection.getGeometries()[0];
        }
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


const styleCluster = (feature: FeatureLike, currentStyle: Style, resolution: number) => {
  if (feature.get("features") === undefined) {
    return currentStyle;
  }

  const clusterSize = feature.get("features").length;

  currentStyle.setText(new Text({
    text: clusterSize.toString(),
    fill: new Fill({
      color: '#fff'
    })
  }));

  currentStyle.setImage(new Circle({
    radius: 10,
    stroke: new Stroke({
      color: '#fff',
    }),
    fill: new Fill({
      color: '#265E78'
    })
  }));


  return currentStyle;
};

const styleSelectedCluster = (feature: FeatureLike, currentStyle: Style, resolution: number) => {
  currentStyle.setText(new Text({
    text: feature.get("features").length.toString(),
    fill: new Fill({
      color: '#fff'
    }),
  }));
  currentStyle.setImage(new Circle({
    radius: 15,
    stroke: new Stroke({
      color: '#fff',
      width: 3,
    }),
    fill: new Fill({
      color: '#265E78'
    })
  }));
  return currentStyle;
}

const model = reactive({
  projection: "EPSG:3857",
  zoom: 5,
  rotation: 0,
  overlayPositionX: 0,
  overlayPositionY: 0,
  selectedFeatures: [] as any[],
  position: transform([props.centerX, props.centerY], 'EPSG:4326', 'EPSG:3857'),
});

const selected = (selectEvent: SelectEvent) => {
  selectEvent.preventDefault();
  if(selectEvent.deselected !== undefined) {
    model.selectedFeatures = [];
  }
  if(selectEvent.type=== 'select') {
    if(selectEvent.selected !== undefined && selectEvent.selected.length > 0) {
      model.selectedFeatures = selectEvent.selected[0].get("features");
      let geometry = model.selectedFeatures[0].getGeometry();
      if(geometry?.getType()=="Point") {
        model.overlayPositionX = (geometry as Point).getCoordinates()[0];
        model.overlayPositionY = (geometry as Point).getCoordinates()[1];
      }
      return;
    }
  }


};

</script>

<style scoped>
@import 'vue3-openlayers/dist/vue3-openlayers.css';

</style>
<template>

  <ol-map
    class="map-with-metadata"
    :loadTilesWhileAnimating="true"
    :loadTilesWhileInteracting="true">

    <ol-view
      ref="view"
      :center="model.position"
      :rotation="model.rotation"
      :zoom="model.zoom"
      :projection="model.projection"
    />

    <ol-tile-layer>
      <ol-source-osm/>
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
      :features="model.selectedFeaturesIntern"
      @select="selected">


      <ol-style :overrideStyleFunction="styleSelectedCluster"/>

    </ol-interaction-select>

    <ol-overlay
      v-if="model.selectedFeatures.length > 0"
      :position="[model.overlayPositionX, model.overlayPositionY, 500, 500]"
      :autoPan="true"
    >
      <div class="overlay-content" :key="model.selectionVersion">
        <slot name="metadata" :solrdocs="selectedProperties"></slot>
      </div>
    </ol-overlay>


  </ol-map>
</template>

<script setup lang="ts">


import {transform} from 'ol/proj';
import {WKT} from 'ol/format';

import Feature, {type FeatureLike} from "ol/Feature";
import type {SelectEvent} from "ol/interaction/Select";
import {Style, Circle, Stroke, Fill, Text} from "ol/style";

import type GeometryCollection from "ol/geom/GeometryCollection";
import {Point} from "ol/geom";
import Collection from "ol/Collection";


const props = defineProps<{
  // solr response json whose docs carry a WKT field (see wktField). The field may hold plain
  // POINTs or a GEOMETRYCOLLECTION; collections are expanded into one marker per geometry.
  solrResponse: any,
  centerX: number,
  centerY: number,
  // name of the solr field holding the WKT geometry; defaults to the soviet-survivors field
  wktField?: string,
}>();

const slots = defineSlots<{
  metadata: (scope: { solrdocs: any[] }) => any
}>()

const emit = defineEmits<{
  // fired whenever the cluster selection changes; carries the selected docs so callers
  // can lazily load extra data (e.g. titles) for just those docs
  select: [solrdocs: any[]]
}>()

const wktFieldName = computed(() => props.wktField ?? "common.mods.coordinates");
const wkt = new WKT();

const selectedProperties = computed(() => {
  return model.selectedFeatures.map((feature: Feature) => {
    return feature.getProperties().properties;
  });
})


const convertedSolrDocument = computed(() => {
  if (!props.solrResponse || props.solrResponse.response === undefined || props.solrResponse.response.numFound == 0) {
    return [];

  }
  const field = wktFieldName.value;
  const convertedFeatures = [];
  for (const solrDoc of props.solrResponse.response.docs) {
    if (!solrDoc[field]) {
      continue;
    }

    for (const coord of solrDoc[field]) {
      const geometry = wkt.readGeometry(coord, {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857'
      });

      if (!geometry) {
        continue;
      }

      // a single doc may reference several places via a GEOMETRYCOLLECTION; render each as
      // its own marker so all places appear on the map (all pointing back to the same doc).
      const geometries = geometry.getType() === "GeometryCollection"
        ? (geometry as GeometryCollection).getGeometries()
        : [geometry];

      for (const singleGeometry of geometries) {
        convertedFeatures.push(new Feature({
          geometry: singleGeometry,
          properties: solrDoc
        }));
      }
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
  // bumped on every new cluster selection; used as the overlay content :key so the metadata
  // panel remounts fresh (resetting any scroll position from the previously opened cluster)
  selectionVersion: 0,
  selectedFeaturesIntern: new Collection([]),
  selectedFeatures: [] as any[],
  position: transform([props.centerX, props.centerY], 'EPSG:4326', 'EPSG:3857'),
});

const selected = (selectEvent: SelectEvent) => {
  selectEvent.preventDefault();
  if (selectEvent.deselected !== undefined) {
    model.selectedFeatures = [];
  }
  if (selectEvent.type === 'select'
    && selectEvent.selected !== undefined && selectEvent.selected.length > 0) {
    model.selectedFeatures = selectEvent.selected[0].get("features");
    model.selectionVersion++;
    const geometry = model.selectedFeatures[0].getGeometry();
    if (geometry?.getType() == "Point") {
      model.overlayPositionX = (geometry as Point).getCoordinates()[0];
      model.overlayPositionY = (geometry as Point).getCoordinates()[1];
    }
  }
  emit('select', selectedProperties.value);
};

defineExpose({
  unselectAll: () => {
    model.selectedFeaturesIntern.clear();

    do {
      model.selectedFeatures = [];
    } while (model.selectedFeatures.length > 0);
  }
})

</script>

<style scoped>
@import 'vue3-openlayers/dist/vue3-openlayers.css';

.map-with-metadata {
  height: 600px;
  max-height: 90vh;
}

</style>

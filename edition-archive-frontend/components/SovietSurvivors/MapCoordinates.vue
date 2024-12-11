<template>
  <ol-map     :loadTilesWhileAnimating="true"
                       :loadTilesWhileInteracting="true"
                       style="height:400px">

    <ol-view
      ref="view"
      :center="center"
      :rotation="model.rotation"
      :zoom="model.zoom"
      :projection="model.projection"
    />

    <ol-tile-layer>
      <ol-source-osm />
    </ol-tile-layer>

    <ol-vector-layer>
      <ol-source-vector>
        <ol-feature>
          <ol-geom-point :coordinates="center" />
          <ol-style>
            <ol-style-circle :radius="6">
              <ol-style-fill color="rgb(0, 105, 120)"></ol-style-fill>
            </ol-style-circle>
          </ol-style>
        </ol-feature>
      </ol-source-vector>
    </ol-vector-layer>

  </ol-map>
</template>

<script setup lang="ts">



const props = defineProps<{
  coordinates: string,
}>()

const center = computed(() => {
  const coordinates = props.coordinates.split(" ");
  return [parseFloat(coordinates[0]), parseFloat(coordinates[1])];
});


const model = reactive({
  projection: "EPSG:4326",
  zoom: 8,
  rotation: 0
});

</script>

<style scoped>
@import 'vue3-openlayers/dist/vue3-openlayers.css';

</style>
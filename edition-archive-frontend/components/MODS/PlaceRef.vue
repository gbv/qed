<template>
  <div>
    <a v-if="isGeoNames"
       href="#"
       class="bi bi-geo-alt bi-interactive"
       v-on:click.prevent="model.show = !model.show"> </a>
    <span v-else class="bi bi-geo-alt"> </span>
    <span>{{ placeTerm }}</span>

    <div v-if="model.show && isGeoNames" class="popout text-start position-relative">
      <a class="close-btn" href="#hide" v-on:click.prevent="model.show = false"><i class="bi bi-x-circle"></i></a>
      <client-only>
        <TeiGEONamesRefResolver :ref-attribute="placeIdentifier!" />
      </client-only>
    </div>
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  placeTerm?: string,
  placeIdentifier?: string,
}>();

const model = reactive({
  show: false as boolean,
});

const isGeoNames = computed(() => {
  const ref = props.placeIdentifier;
  if (!ref) return false;
  return ref.startsWith("https://sws.geonames.org/")
    || ref.startsWith("https://geonames.org/")
    || ref.startsWith("https://www.geonames.org/");
});
</script>

<style scoped>

.popout {
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px;
  padding: 1rem;
  margin-top: 1rem;
  margin-bottom: 1rem;
  min-height: 5rem;
}

.close-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
}
</style>

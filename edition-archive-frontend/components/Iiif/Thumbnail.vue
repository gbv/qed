<template>
  <div ref="rootEl" class="doc_thumbnail">
    <img v-if="exists" :src="thumbnailURL" :alt="alt" class="img-thumbnail">
  </div>
</template>

<script setup lang="ts">

const props = withDefaults(defineProps<{
  // base application URL (must end with a slash), e.g. the project's $ditavURL()/$sovietSurviorsURL()
  appUrl: string,
  // mods object or derivate id whose main image should be shown
  id: string,
  // IIIF size parameter
  size?: string,
  alt?: string,
}>(), {
  size: "!300,300",
  alt: "thumbnail",
});

const rootEl = ref<HTMLElement | null>(null);
// flips true once the thumbnail scrolls into the viewport; gates the info.json request
const visible = ref(false);

const {exists, thumbnailURL} = useIiifThumbnail(() => props.appUrl, () => props.id, {
  size: props.size,
  enabled: visible,
});

let observer: IntersectionObserver | null = null;

onMounted(() => {
  if (typeof IntersectionObserver === "undefined") {
    // no observer support (e.g. SSR/old browsers): load eagerly
    visible.value = true;
    return;
  }
  observer = new IntersectionObserver((entries) => {
    if (entries.some((entry) => entry.isIntersecting)) {
      visible.value = true;
      observer?.disconnect();
      observer = null;
    }
  }, {rootMargin: "100px"});
  if (rootEl.value) {
    observer.observe(rootEl.value);
  }
});

onBeforeUnmount(() => {
  observer?.disconnect();
  observer = null;
});

</script>

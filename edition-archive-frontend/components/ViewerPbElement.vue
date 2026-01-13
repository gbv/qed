<template>
  <div class="pb-element" ref="pbElement">
    <span v-if="n">~
      <span class="page-number">{{ n }}</span>
      <a v-if="facs && showImageIcon" v-on:click="imageIconClicked" :title="$t('metadata.showImage')"><span
        class="bi bi-image imabe-icon"> </span></a> ~</span>
  </div>
</template>

<script setup lang="ts">

import type {TEIElement} from "~/api/tei.model";

const props = defineProps<{
  pbElement: TEIElement,
  viewerRoot: HTMLElement | null,
  showImageIcon?: boolean
}>();

const emit = defineEmits<{
  (e: 'page-break-in-view', pb: TEIElement): void,
  (e: 'image-icon-clicked', pb: TEIElement): void
}>();

const pbElement = useTemplateRef("pbElement");

const {$tei} = useTei();

const n = computed(() => {
  return props.pbElement.attributes.n || '';
});

const facs = computed(() => {
  return props.pbElement.attributes.facs || null;
});

const imageIconClicked = () => {
  emit('image-icon-clicked', props.pbElement);
}

const intersectionChanged = (entries: IntersectionObserverEntry[]) => {
  entries.forEach((entry) => {
    if (entry.isIntersecting) {
      console.log("Page break in view:", props.pbElement);
      emit('page-break-in-view', props.pbElement);
    }
  });
}

let observer: IntersectionObserver | null = null;

const initializeObserver = () => {
  if (props.viewerRoot && pbElement.value != null) {
    observer = new IntersectionObserver(intersectionChanged, {
      root: props.viewerRoot,
      threshold: 0.1
    });
    observer.observe(pbElement.value);
  }
}

const uninitializeObserver = () => {
  if (observer) {
    observer.disconnect();
    observer = null;
  }
}

if (import.meta.client) {


  onMounted(() => {
    initializeObserver();
  });

  onUnmounted(() => {
    uninitializeObserver();
  });

}

watch(() => props.viewerRoot, (newVal, oldVal) => {
  uninitializeObserver();
  initializeObserver();
});

watch(() => pbElement.value, (newVal, oldVal) => {
  uninitializeObserver();
  initializeObserver();
});


</script>


<style scoped>

.pb-element {
  text-align: center;
}

.page-number {
  font-weight: bold;
}

.imabe-icon {
  cursor: pointer;
  margin-left: 5px;
}


</style>
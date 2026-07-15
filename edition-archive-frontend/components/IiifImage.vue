<template>
  <div class="iiif-image-wrapper" ref="containerRef">
    <img
      v-if="imageUrl"
      :src="imageUrl"
      :alt="alt"
      class="iiif-image"
      @load="onImageLoad"
    />
    <div v-else-if="isLoading" class="iiif-image-placeholder">
      <span>{{ $t('gpo.viewer.image.loading') }}</span>
    </div>
    <div v-else-if="error" class="iiif-image-error">
      <em>{{ $t('gpo.viewer.image.error') }}</em>
    </div>
    <div v-else class="iiif-image-placeholder">
      <em>{{ $t('gpo.viewer.image.notAvailable') }}</em>
    </div>
    <div v-if="imageUrl && isImageLoading" class="iiif-image-loading-overlay" role="status" :aria-label="$t('gpo.viewer.image.loading')">
      <div class="spinner-border text-primary" aria-hidden="true"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface IIIFInfo {
  width: number;
  height: number;
}

const props = defineProps<{
  appUrl: string;
  derivateId: string;
  imagePath: string;
  alt?: string;
}>();

const emit = defineEmits<{
  (e: 'loaded'): void;
}>();

const containerRef = useTemplateRef<HTMLDivElement>("containerRef");
const isLoading = ref(true);
const isImageLoading = ref(false);
const error = ref<string | null>(null);
const iiifInfo = ref<IIIFInfo | null>(null);
const imageUrl = ref<string | null>(null);
let lastRequestedWidth = 0;

const encodedImageIdentifier = computed(() => encodeURIComponent(props.imagePath));

const iiifBaseUrl = computed(() =>
  `${props.appUrl}api/iiif/image/v2/${props.derivateId}%2F${encodedImageIdentifier.value}`
);

const infoJsonUrl = computed(() => `${iiifBaseUrl.value}/info.json`);

const loadIiifInfo = async () => {
  try {
    isLoading.value = true;
    error.value = null;
    lastRequestedWidth = 0;
    const response = await fetch(infoJsonUrl.value);
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    iiifInfo.value = await response.json();
    updateImageUrl();
  } catch (err) {
    console.error('Fehler beim Laden von IIIF info.json:', err);
    error.value = 'Bildinformationen konnten nicht geladen werden';
  } finally {
    isLoading.value = false;
  }
};

const measureContainerWidth = (): number => {
  if (!containerRef.value) return 600;
  const rect = containerRef.value.getBoundingClientRect();
  let width = rect.width || containerRef.value.clientWidth || 0;
  if (width === 0 && containerRef.value.parentElement) {
    width = containerRef.value.parentElement.getBoundingClientRect().width || 0;
  }
  return width || 600;
};

const updateImageUrl = () => {
  if (!iiifInfo.value) return;
  const containerWidth = measureContainerWidth();
  const dpr = window.devicePixelRatio || 1;
  const targetWidth = Math.min(
    Math.max(1, Math.round(containerWidth * dpr)),
    iiifInfo.value.width
  );
  // Vermeidet unnötige Re-Requests bei Mikro-Änderungen
  if (Math.abs(targetWidth - lastRequestedWidth) < 8 && imageUrl.value) {
    return;
  }
  lastRequestedWidth = targetWidth;
  const nextUrl = `${iiifBaseUrl.value}/full/${targetWidth},/0/default.jpg`;
  if (nextUrl !== imageUrl.value) {
    isImageLoading.value = true;
    imageUrl.value = nextUrl;
  }
};

const onImageLoad = () => {
  isImageLoading.value = false;
  emit('loaded');
};

let resizeObserver: ResizeObserver | null = null;

watch(() => [props.derivateId, props.imagePath], () => {
  loadIiifInfo();
}, { immediate: true });

onMounted(() => {
  if (containerRef.value) {
    resizeObserver = new ResizeObserver(() => {
      if (iiifInfo.value) updateImageUrl();
    });
    resizeObserver.observe(containerRef.value);
  }
  nextTick(() => {
    if (iiifInfo.value) updateImageUrl();
  });
});

onUnmounted(() => {
  if (resizeObserver) {
    resizeObserver.disconnect();
    resizeObserver = null;
  }
});
</script>

<style scoped>
.iiif-image-wrapper {
  position: relative;
  width: 100%;
}

.iiif-image {
  display: block;
  width: 100%;
  height: auto;
}

.iiif-image-placeholder,
.iiif-image-error {
  padding: 2rem;
  text-align: center;
  color: #666;
}

.iiif-image-error {
  color: #dc3545;
}

.iiif-image-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 2rem;
  background: rgba(255, 255, 255, 0.4);
  pointer-events: none;
  z-index: 1;
}
</style>

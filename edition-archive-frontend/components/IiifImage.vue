<template>
  <div class="iiif-image-wrapper">
    <!-- Normaler Anzeigemodus -->
    <div v-if="!isMaximized" class="iiif-image-container" ref="containerRef">
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

      <!-- Maximize button (nur wenn Bild vorhanden) -->
      <button
        v-if="imageUrl && showMaximizeButton"
        type="button"
        :aria-label="$t('gpo.viewer.image.maximize')"
        @click="toggleMaximize"
        class="btn btn-sm btn-light position-absolute top-0 end-0 m-2"
      >
        <i class="bi bi-arrows-angle-expand" aria-hidden="true"></i>
      </button>
    </div>

    <!-- Fullscreen overlay -->
    <Teleport to="body">
      <div
        v-if="isMaximized"
        class="iiif-image-fullscreen"
        @click.self="toggleMaximize"
      >
        <button
          type="button"
          :aria-label="$t('gpo.viewer.image.close')"
          @click="toggleMaximize"
          class="btn btn-light position-fixed top-0 end-0 m-3"
        >
          <i class="bi bi-x-lg" aria-hidden="true"></i>
        </button>
        <div class="iiif-image-fullscreen-inner">
          <img
            v-if="fullscreenImageUrl"
            :src="fullscreenImageUrl"
            :alt="alt"
            class="iiif-image-fullscreen-img"
          />
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
interface IIIFInfo {
  width: number;
  height: number;
  "@context"?: string;
  "@id"?: string;
  protocol?: string;
  profile?: any;
  sizes?: Array<{ width: number; height: number }>;
  tiles?: any[];
}

const props = defineProps<{
  appUrl: string;
  derivateId: string;
  imagePath: string;
  alt?: string;
  showMaximizeButton?: boolean;
}>();

const containerRef = useTemplateRef<HTMLDivElement>("containerRef");
const isMaximized = ref(false);
const isLoading = ref(true);
const error = ref<string | null>(null);
const iiifInfo = ref<IIIFInfo | null>(null);
const imageUrl = ref<string | null>(null);
const fullscreenImageUrl = ref<string | null>(null);

// Encode image path für IIIF (/ zu %2F)
const encodedImageIdentifier = computed(() => {
  return encodeURIComponent(props.imagePath);
});

// Base URL für IIIF
const iiifBaseUrl = computed(() => {
  return `${props.appUrl}api/iiif/image/v2/${props.derivateId}%2F${encodedImageIdentifier.value}`;
});

// Info.json URL
const infoJsonUrl = computed(() => {
  return `${iiifBaseUrl.value}/info.json`;
});

// IIIF Info laden
const loadIiifInfo = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    const response = await fetch(infoJsonUrl.value);
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    iiifInfo.value = await response.json();
    updateImageUrls();
  } catch (err) {
    console.error('Fehler beim Laden von IIIF info.json:', err);
    error.value = 'Bildinformationen konnten nicht geladen werden';
  } finally {
    isLoading.value = false;
  }
};

// Berechne optimale Bildgröße basierend auf Container
const calculateOptimalSize = (containerWidth: number, containerHeight: number): { width: number; height: number } => {
  if (!iiifInfo.value) {
    return { width: 800, height: 600 }; // Fallback
  }

  const { width: originalWidth, height: originalHeight } = iiifInfo.value;
  const aspectRatio = originalWidth / originalHeight;

  // Device Pixel Ratio berücksichtigen für schärfere Bilder
  const dpr = window.devicePixelRatio || 1;
  const targetWidth = containerWidth * dpr;
  const targetHeight = containerHeight * dpr;

  // Berechne Größe unter Beibehaltung des Seitenverhältnisses
  let width = targetWidth;
  let height = width / aspectRatio;

  if (height > targetHeight) {
    height = targetHeight;
    width = height * aspectRatio;
  }

  // Nicht größer als Original
  width = Math.min(width, originalWidth);
  height = Math.min(height, originalHeight);

  return {
    width: Math.round(width),
    height: Math.round(height)
  };
};

// Update Image URLs
const updateImageUrls = () => {
  if (!iiifInfo.value) return;

  // Normale Ansicht
  let containerWidth = 600;
  let containerHeight = 500;

  if (containerRef.value) {
    const rect = containerRef.value.getBoundingClientRect();
    // Verwende clientWidth/clientHeight als Fallback, falls getBoundingClientRect 0 zurückgibt
    containerWidth = rect.width || containerRef.value.clientWidth || 600;
    containerHeight = rect.height || containerRef.value.clientHeight || 500;

    // Wenn immer noch 0, verwende parent-Element
    if (containerWidth === 0 && containerRef.value.parentElement) {
      const parentRect = containerRef.value.parentElement.getBoundingClientRect();
      containerWidth = parentRect.width || 600;
      containerHeight = parentRect.height || 500;
    }
  }

  const size = calculateOptimalSize(containerWidth, containerHeight);
  imageUrl.value = `${iiifBaseUrl.value}/full/!${size.width},${size.height}/0/default.jpg`;

  // Fullscreen Ansicht (höhere Auflösung)
  const fullscreenSize = calculateOptimalSize(window.innerWidth - 100, window.innerHeight - 100);
  fullscreenImageUrl.value = `${iiifBaseUrl.value}/full/!${fullscreenSize.width},${fullscreenSize.height}/0/default.jpg`;
};

const toggleMaximize = () => {
  isMaximized.value = !isMaximized.value;
};

const onImageLoad = () => {
  // Bild erfolgreich geladen
};

// ESC-Taste schließt die Vollbild-Ansicht
const onKeyDown = (ev: KeyboardEvent) => {
  if (ev.key === 'Escape' && isMaximized.value) {
    isMaximized.value = false;
  }
};

// Resize handler für responsive Anpassung
const onResize = () => {
  if (iiifInfo.value && !isMaximized.value) {
    updateImageUrls();
  }
};

let resizeObserver: ResizeObserver | null = null;

// Watch für Props-Änderungen
watch(() => [props.derivateId, props.imagePath], () => {
  loadIiifInfo();
}, { immediate: true });

// Watch für Fullscreen-Änderungen
watch(isMaximized, (newVal) => {
  if (newVal && iiifInfo.value) {
    // Neu berechnen für Fullscreen
    const fullscreenSize = calculateOptimalSize(window.innerWidth - 100, window.innerHeight - 100);
    fullscreenImageUrl.value = `${iiifBaseUrl.value}/full/!${fullscreenSize.width},${fullscreenSize.height}/0/default.jpg`;
  }
});

onMounted(() => {
  window.addEventListener('keydown', onKeyDown);
  window.addEventListener('resize', onResize);

  // ResizeObserver für Container-Größenänderungen
  if (containerRef.value) {
    resizeObserver = new ResizeObserver(() => {
      if (iiifInfo.value && !isMaximized.value) {
        updateImageUrls();
      }
    });
    resizeObserver.observe(containerRef.value);
  }

  // Falls Container beim Mount schon eine Größe hat, URL neu berechnen
  nextTick(() => {
    if (iiifInfo.value) {
      updateImageUrls();
    }
  });
});

onUnmounted(() => {
  window.removeEventListener('keydown', onKeyDown);
  window.removeEventListener('resize', onResize);

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
  height: 100%;
}

.iiif-image-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  max-height: 500px;
  overflow: auto;
}

.iiif-image {
  max-width: 100%;
  max-height: 500px;
  height: auto;
  width: auto;
  object-fit: contain;
  display: block;
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

/* Fullscreen styles */
.iiif-image-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 2000;
  padding: 1rem;
  box-sizing: border-box;
  overflow: auto;
}

.iiif-image-fullscreen-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 2rem);
  width: 100%;
  box-sizing: border-box;
  padding-top: 1rem;
  padding-bottom: 1rem;
}

.iiif-image-fullscreen-img {
  max-width: calc(100vw - 4rem);
  max-height: calc(100vh - 8rem);
  width: auto !important;
  height: auto !important;
  object-fit: contain !important;
  display: block;
  margin: 0 auto;
}
</style>


<template>
  <div class="youtube-embed-wrapper">
    <!-- Consent Overlay -->
    <div v-if="!consentGiven" class="youtube-consent-overlay">
      <NuxtImg
        v-if="previewImage"
        :src="previewImage"
        :alt="$t('metadata.youtubeConsent.warning')"
        class="youtube-preview-image"
        loading="lazy"
      />
      <p class="mb-3">{{ $t('metadata.youtubeConsent.warning') }}</p>
      <button
        class="btn btn-light mb-3"
        @click="giveConsent"
      >
        {{ $t('metadata.youtubeConsent.accept') }}
      </button>
      <NuxtLink :to="privacyPolicyUrl" class="text-white text-decoration-underline">
        {{ $t('metadata.youtubeConsent.privacyLink') }}
      </NuxtLink>
    </div>

    <!-- YouTube iFrame -->
    <div v-else class="youtube-iframe-container">
      <iframe
        :src="embedUrl"
        :title="title || 'YouTube video player'"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
        allowfullscreen
        class="youtube-iframe"
      ></iframe>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  youtubeUrl: string
  previewImage?: string
  title?: string
  privacyPolicyUrl?: string
}

const props = withDefaults(defineProps<Props>(), {
  previewImage: undefined,
  title: undefined,
  privacyPolicyUrl: '/datenschutz#eingebettete-youtube-videos'
})

const consentGiven = ref(false)

const embedUrl = computed(() => {
  // Extract video ID from various YouTube URL formats
  const videoId = extractVideoId(props.youtubeUrl)
  if (videoId) {
    return `https://www.youtube-nocookie.com/embed/${videoId}`
  }
  return ''
})

function extractVideoId(url: string): string | null {
  // Handle various YouTube URL formats
  const patterns = [
    /(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([^&\n?#]+)/,
    /^([a-zA-Z0-9_-]{11})$/ // Direct video ID
  ]

  for (const pattern of patterns) {
    const match = url.match(pattern)
    if (match && match[1]) {
      return match[1]
    }
  }

  return null
}

function giveConsent() {
  consentGiven.value = true
}
</script>

<style scoped>
.youtube-embed-wrapper {
  position: relative;
  width: 100%;
  padding-bottom: 56.25%; /* 16:9 aspect ratio */
  background-color: #000;
}

.youtube-consent-overlay {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.5);
  color: #fff;
  z-index: 9;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  flex-direction: column;
}

.youtube-preview-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.3;
  z-index: -1;
}

.youtube-iframe-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.youtube-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>


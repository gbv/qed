// DynamicComponent.vue
<script setup lang="ts">
import type { Component } from '@vue/runtime-core'
import { NuxtLink, GalliaPontificiaOnlineEditorLink, ChangeLanguage } from '#components'

const props = defineProps<{
  template?: string
}>()

const renderer = computed(() => () => h({
  template: props.template,
  components: {
    NuxtLink, GalliaPontificiaOnlineEditorLink, ChangeLanguage
  },
}))
</script>

<template>
  <component :is="renderer" />
</template>
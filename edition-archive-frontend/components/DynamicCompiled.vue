// DynamicComponent.vue
<script setup lang="ts">
import type { Component } from '@vue/runtime-core'
import { NuxtLink, GalliaPontificaOnlineEditorLink } from '#components'

const props = defineProps<{
  template?: string
}>()

const renderer = computed(() => () => h({
  template: props.template,
  components: {
    NuxtLink, GalliaPontificaOnlineEditorLink
  },
}))
</script>

<template>
  <component :is="renderer" />
</template>
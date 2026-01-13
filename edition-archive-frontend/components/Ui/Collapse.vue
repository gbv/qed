<template>
  <div class="d-inline ms-2">
    <button class="btn btn-primary btn-sm" @click="toggle">
      {{ open ? ($t(labelKeyOpen) || $t(labelKey)) : $t(labelKey) }}
    </button>
    <div v-show="open" class="mt-2 w-100">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  initiallyOpen?: boolean,
  labelKey: string,
  labelKeyOpen: string
}>();

const open = ref(props.initiallyOpen);
const toggle = () => { open.value = !open.value };
</script>

<style scoped>
</style>

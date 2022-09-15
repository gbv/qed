<template>
  <span>{{ $t($props.key) }}</span>
</template>

<script lang="ts" setup>
defineProps(["key"])
</script>

<style scoped>

</style>
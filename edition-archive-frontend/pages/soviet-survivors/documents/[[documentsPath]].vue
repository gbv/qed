<template>
  <SovietSurvivorsLayout>

    <template #content>
      <SovietSurvivorsDocument />
    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">
  import {createError} from 'h3'
  const route = useRoute();
</script>

<style scoped>
</style>
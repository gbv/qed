<template>
  <GazinLayout :headline="$t('gazin.mainMenu.project')">

    <template #content>
      <CMSPage :slug="$route.path"/>
    </template>

    <template #menu>
      <GazinMenu/>
    </template>

  </GazinLayout>
</template>

<script setup lang="ts">
  const route = useRoute();
</script>

<style scoped>
</style>
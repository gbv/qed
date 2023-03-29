<template>
  <SovietSurvivorsLayout :headline="$t('sosu.mainMenu.about')">

    <template #content>
      <CMSPage :slug="$route.path"/>
    </template>

    <template #menu>
      <SovietSurvivorsMenu/>
    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">
  const route = useRoute();
</script>

<style scoped>
</style>
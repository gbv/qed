<template>
  <SovietSurvivorsLayout :headline="$t('lod.mainMenu.about')">

    <template #content>
      <CMSPage default-language="en" :languages="['de', 'en', 'ru']" :slug="`/qed${path}`"/>
    </template>

    <template #menu>
      <SovietSurvivorsMenu/>
    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">
  const {path} = useRoute();
</script>

<style scoped>
</style>
<template>
  <LoDLayout :headline="$t('sosu.mainMenu.about')">

    <template #content>
      <CMSPage :slug="$route.path"/>
    </template>

    <template #menu>
      <LoDMenu/>
    </template>

  </LoDLayout>
</template>

<script setup lang="ts">
  const route = useRoute();
</script>

<style scoped>
</style>
<template>
  <LoDLayout :headline="$t('lod.mainMenu.about')">

    <template #content>
      <CMSPage :slug="`/qed${path}`"/>
    </template>

    <template #menu>
      <LoDMenu/>
    </template>

  </LoDLayout>
</template>

<script setup lang="ts">
  const {path} = useRoute();
</script>

<style scoped>
</style>
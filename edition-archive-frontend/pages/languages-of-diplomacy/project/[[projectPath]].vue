<template>
  <LoDLayout :headline="$t('lod.mainMenu.about')">

    <template #content>
      <CMSPage default-language="en" :languages="['de', 'en']" :slug="`/qed${path}`"/>
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
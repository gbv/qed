<template>
  <GazinLayout :headline="$t('gazin.mainMenu.project')">

    <template #content>
      <CMSPage :slug="`/qed${path}`"/>
    </template>

    <template #menu>
      <GazinMenu/>
    </template>

  </GazinLayout>
</template>

<script setup lang="ts">
  const {path} = useRoute();
</script>

<style scoped>
</style>
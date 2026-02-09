<template>
  <LoDLayout>

    <template #content>
      <CMSPage default-language="en" :languages="['de', 'en', 'fr', 'ru']" :slug="`/qed${path}`"/>
    </template>

  </LoDLayout>
</template>

<script lang="ts" setup>

const {path} = useRoute()

</script>
<style scoped>

</style>

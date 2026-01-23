<template>
  <GazinLayout>

    <template #content>
      <CMSPage default-language="en" :languages="['de', 'en', 'ku', 'tr']" :slug="`/qed${path}`"/>
    </template>

  </GazinLayout>
</template>

<script lang="ts" setup>

const {path} = useRoute()

</script>
<style scoped>

</style>
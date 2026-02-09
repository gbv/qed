<template>
  <GazinLayout>

    <template #content>
      <CMSPage default-language="en" :languages="['de', 'en', 'ku', 'tr']" :slug="`/qed${computedPath}`"/>
    </template>

  </GazinLayout>
</template>

<script lang="ts" setup>

const {path} = useRoute()

const computedPath = computed(() => {
  if (path == '/gazin') {
    return '/gazin/'
  }
  return path;
})

</script>
<style scoped>

</style>

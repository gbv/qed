<template>
  <GazinLayout>

    <template #content>
      <CMSPage :slug="$route.path"/>
    </template>

  </GazinLayout>
</template>

<script lang="ts" setup>

const {path} = useRoute()

</script>
<style scoped>

</style>
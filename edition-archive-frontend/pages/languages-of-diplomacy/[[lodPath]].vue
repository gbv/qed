<template>
  <LoDLayout>

    <template #content>
      <CMSPage :slug="$route.path"/>
    </template>

  </LoDLayout>
</template>

<script lang="ts" setup>

const {path} = useRoute()

</script>
<style scoped>

</style>
<template>
  <SovietSurvivorsLayout>

    <template #content>
      <CMSPage :slug="$route.path"/>
    </template>

  </SovietSurvivorsLayout>
</template>

<script lang="ts" setup>

const {path} = useRoute()

</script>
<style scoped>

</style>
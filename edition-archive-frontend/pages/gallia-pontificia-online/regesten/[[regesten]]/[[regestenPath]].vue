<template>
  <GalliaPontificiaOnlineLayout>
    <template #content>
      <CMSPage :slug="$route.path" />
    </template>

    <template #menu>
      <GalliaPontificiaOnlineMenu />
    </template>

  </GalliaPontificiaOnlineLayout>
</template>
<script setup lang="ts">
const {path} = useRoute()
</script>
<style scoped>

</style>
<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
      <CMSPage :slug="$route.path" />
    </template>

    <template #menu>
      <GalliaPontificaOnlineMenu />
    </template>

  </GalliaPontificaOnlineLayout>
</template>
<script setup lang="ts">
const {path} = useRoute()
</script>
<style scoped>

</style>
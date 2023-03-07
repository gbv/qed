<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
      <CMSPage :slug="$route.path" />
    </template>

  </GalliaPontificaOnlineLayout>
</template>
<script setup lang="ts">

const {path} = useRoute()

</script>
<style scoped>

</style>
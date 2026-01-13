<template>
  <GalliaPontificiaOnlineLayout>

    <template #content>
      <CMSPage :slug="`/qed${path}`" />
    </template>

  </GalliaPontificiaOnlineLayout>
</template>

<script setup lang="ts">
  const {path} = useRoute()
</script>
<style scoped>

</style>
<template>
  <GalliaPontificiaOnlineLayout>

    <template #content>
      <CMSPage default-language="en" :languages="['de', 'en', 'fr']" :slug="`/qed${path}`" />
    </template>

  </GalliaPontificiaOnlineLayout>
</template>

<script setup lang="ts">
  const {path} = useRoute()
</script>
<style scoped>

</style>
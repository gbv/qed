<template>
  <GalliaPontificiaOnlineLayout>

    <template #content>
      <CMSPage :slug="$route.path" />
    </template>

  </GalliaPontificiaOnlineLayout>
</template>

<script setup lang="ts">
  const {path} = useRoute()
  console.log("gpo");
  //document.body.classList.add("gpo-page");
</script>
<style scoped>

</style>
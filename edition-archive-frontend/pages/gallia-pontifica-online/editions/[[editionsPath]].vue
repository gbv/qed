<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
        <ContentRenderer v-if="data" :value="data"/>
    </template>

  </GalliaPontificaOnlineLayout>
</template>
<script setup lang="ts">
  const route = useRoute();

  const {data, error} = await useAsyncData(`content-${(route.path)}`, () => {
    const p1 = route.path.lastIndexOf("/") === route.path.length - 1 ? route.path + "paris" : route.path;
    return queryContent().where({_path: p1}).findOne()
  })
  if (error.value) {
    showError(
        createError({
          statusCode: 404,
          statusMessage: 'Not Found',
        })
    );
  }
</script>

<style scoped>
</style>
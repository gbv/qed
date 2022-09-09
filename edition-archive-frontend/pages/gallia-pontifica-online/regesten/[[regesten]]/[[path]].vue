<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
          <ContentRenderer v-if="data" :value="data"/>
    </template>

    <template #menu>
        <GalliaPontificaOnlineMenu />
    </template>

  </GalliaPontificaOnlineLayout>
</template>

<script>
  export default {}
</script>

<script setup>
  const route = useRoute();
  import {createError} from 'h3'

  const {data, error} = await useAsyncData(`content-${(route.path)}`, () => {
    const p1 = route.path.lastIndexOf("/") === route.path.length - 1 ? route.path + "startseite" : route.path;
    return queryContent().where({_path: p1}).findOne()
  })
  if (error.value) {
    throwError(
        createError({
          statusCode: 404,
          statusMessage: 'Not Found',
        })
    );
  }
</script>

<style scoped>
</style>
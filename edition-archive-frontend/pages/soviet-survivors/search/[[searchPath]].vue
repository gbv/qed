<template>
  <SovietSurvivorsLayout :headline="$t('search')">

    <template #content>
        <ContentRenderer v-if="data" :value="data"/>
    </template>

    <template #menu>
      <SovietSurvivorsMenu/>
    </template>

  </SovietSurvivorsLayout>
</template>

<script setup lang="ts">
  import {createError} from 'h3'
  const route = useRoute();

  const {data, error} = await useAsyncData(`content-${(route.path)}`, () => {
    const p1 = route.path.lastIndexOf("/") === route.path.length - 1 ? route.path + "search" : route.path;
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
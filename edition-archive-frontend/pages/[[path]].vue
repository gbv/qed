<template>
  <div class="row">
    <div class="col">
      <ContentRenderer v-if="data" :value="data"/>
    </div>
  </div>
</template>

<script setup lang="ts">
  import {createError} from 'h3'

  const {path} = useRoute()

  const {data, error} = await useAsyncData(`content-${path}`, () => {
    const p1 = path.lastIndexOf("/") === path.length - 1 ? path  : path;
    return queryContent().where({_path: p1}).findOne()
  });

  if (error.value) {
    console.error(error.value)
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
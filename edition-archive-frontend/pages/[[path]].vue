<template>
  <div class="row">
    <div class="col">
      <ContentRenderer v-if="data" :value="data"/>
    </div>
  </div>
</template>

<script>
  export default {}
</script>

<script setup>
  const {path} = useRoute()
  import {createError} from 'h3'
  const {data, error} = await useAsyncData(`content-${path}`, () => {
    const p1 = path.lastIndexOf("/") === path.length - 1 ? path + "main" : path;
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
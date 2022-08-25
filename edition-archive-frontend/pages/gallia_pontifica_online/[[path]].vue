<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
        <ContentRenderer v-if="data" :value="data"/>
    </template>

  </GalliaPontificaOnlineLayout>
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
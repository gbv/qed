<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
        <ContentRenderer v-if="data" :value="data"/>
    </template>

  </GalliaPontificaOnlineLayout>
</template>
<script setup lang="ts">
import {createError} from 'h3'

const {path} = useRoute()

const {data, error} = await useAsyncData(`content-${path}`, () => {
  const p1 = path.lastIndexOf("/") === path.length - 1 ? path + "beschreibung" : path;
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
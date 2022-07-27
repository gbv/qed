<template>
  <div class="row">
    <div class="col">
      <h2 class="mb-5">Gallia Pontificia Online</h2>
    </div>
  </div>
  <div class="row">
    <div class="col-3">

      <nav class="navbar mt-5">
        <ul class="navbar-nav">
          <li class="nav-item">
            <a class="nav-link" data-bs-toggle="collapse" href="./">
              {{ $t('startPage') }}
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" data-bs-toggle="collapse" href="explain">
              {{ $t('explain') }}
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" data-bs-toggle="collapse" href="./search">
              {{ $t('search') }}
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" data-bs-toggle="collapse" href="./contact">
              {{ $t('contact') }}
            </a>
          </li>
        </ul>
      </nav>

    </div>
    <div class="col-9">
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
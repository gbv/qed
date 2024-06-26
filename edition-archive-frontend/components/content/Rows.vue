<template>
  <div class="row">
    <div v-for="(content, slot) in $slots" :class="`col-xl-4 col-lg-6 col-12 mt-3 mb-5`">
      <slot :name="slot"/>
    </div>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped>

</style>
<template>
  <div class="row">
    <div v-for="(content, slot) in $slots" :class="`col-${12/Object.keys($slots).length}`">
      <slot :name="slot"/>
    </div>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped>

</style>
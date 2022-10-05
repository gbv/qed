<template>
  <div class="row">
    <div v-for="(content, slot) in $slots" :class="`col-md-${12/Object.keys($slots).length} col-xs-12 mt-2`">
      <slot :name="slot"/>
    </div>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped>

</style>
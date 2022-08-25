<template>
  <span><span v-if="lost">*</span><span v-if="fake">†</span><span v-if="!certainly">?</span>({{ idno }})</span>
</template>

<script setup>

defineProps(['idno', 'lost', 'fake', 'certainly'])

</script>

<style scoped>

</style>
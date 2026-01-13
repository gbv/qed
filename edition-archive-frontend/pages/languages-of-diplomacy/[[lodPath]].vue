<template>
  <LoDLayout>

    <template #content>
      <CMSPage :slug="`/qed${path}`"/>
    </template>

  </LoDLayout>
</template>

<script lang="ts" setup>

const {path} = useRoute()

</script>
<style scoped>

</style>
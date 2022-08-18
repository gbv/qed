<template>
  <form v-on:submit.prevent="submit">
    <div class="row">
      <div class="col-10">
        <input type="search" class="form-control" v-model="searchString" :placeholder="$t('search_placeholder')">
      </div>
      <div class="col-auto">
        <button type="submit" class="btn btn-primary">{{ $t("search_submit") }}</button>
      </div>
    </div>
  </form>
</template>

<script setup>

const emit = defineEmits(["search"])
const props = defineProps(["searchString"]);

const submit = () => {
  emit("search", {searchString: props.searchString})
}

</script>

<style scoped>

</style>
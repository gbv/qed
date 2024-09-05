<template>
  <div class="row">
    <div class="col-12">
      <form v-on:submit.prevent="submit" >
        <div class="input-group">
          <input type="search" class="form-control" v-model="searchString">
          <button type="submit" class="btn btn-primary">
            {{ $t("search.submit") }}
            <i class="bi bi-search ms-1"></i>
          </button>
        </div>
      </form>
    </div>
  </div>

</template>

<script setup>

const emit = defineEmits(["search"])
const props = defineProps(["searchString"]);
const searchString = ref(props.searchString);

watch(() => props.searchString, (newSearchString) => {
  searchString.value = newSearchString;
});

const submit = () => {
  if (searchString != null) {
    emit("search", {searchString: searchString.value})
  }
}

</script>

<style scoped>

</style>
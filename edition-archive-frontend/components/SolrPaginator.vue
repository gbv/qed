<template>
  <nav class="solr-paginator" :aria-label="$t('search.pagination')" v-if="count>numPerPage">
    <ul class="pagination justify-content-center">
      <li class="page-item">
        <a class="page-link" :class="start>0?'':'disabled'" v-on:click.prevent="pageChanged(current-1)"
           :title="$t('search.pagePrev')" href="#">
          &laquo;
        </a>
      </li>
      <li class="page-item" v-for="page in shownPages">
        <a class="page-link"
           :class="current === page ?'active':''"
           v-on:click.prevent="pageChanged(page)"
           :title="$t('seach.pageGoTo', {page})"
           href="#">{{ page }}</a>
      </li>
      <li class="page-item">
        <a class="page-link" :class="(start+numPerPage)<count?'':'disabled'" v-on:click.prevent="pageChanged(current+1)"
           :title="$t('search.pageNext')" href="#">
          &raquo;
        </a>
      </li>
    </ul>
  </nav>
</template>

<script setup>
const emit = defineEmits(["pageChanged"]);
const props = defineProps(['count', "start", "numPerPage"]);

const current = computed(() => {
  return Math.floor(props.start / props.numPerPage) + 1;
});


const shownPages = computed(() => {
  const lastPage = Math.ceil(props.count / props.numPerPage);
  const currentPlusSix = (props.start + (6 * props.numPerPage)) / props.numPerPage;
  const last = Math.min(lastPage, currentPlusSix);

  const currentMinusFour = (props.start - (4 * props.numPerPage)) / props.numPerPage;
  const first = Math.max(currentMinusFour, 1);
  const pages = [];
  for (let i = first; i <= last; i++) {
    pages.push(i);
  }
  return pages;
});


const pageChanged = (page) => {
  emit("pageChanged", page)
};

</script>

<style scoped>
li.page-item {
  flex-grow: 1;
  text-align: center;
}

ul.pagination {
  justify-content: stretch;
}
</style>
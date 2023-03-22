<template>xxx
  <span class="popout-wrapper position-relative d-inline">
    <a href="#" v-on:click.prevent="model.show ? hideDekretale():showDekretale()">
      <i class="bi bi-book"></i>
    </a>
    <slot/>
    <div v-if="model.show" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hideDekretale()"><i class="bi bi-x-circle"></i></a>
      <div class="link-list">
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/dekretale?dekretaleKey=${props.dekretaleKey}`">{{$t('search')}}</nuxt-link>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/dekretale/#${props.dekretaleKey}`">{{$t('regest_dekretale_index')}}</nuxt-link>
      </div>
    </div>
  </span>
</template>
<script lang="ts" setup>

const props = defineProps<{
  dekretaleKey: string,
  skipDisplayName?: boolean,
}>();


const route = useRoute();
const model = reactive({ show: false });

const hideDekretale = () => {
  model.show = false;
};

const showDekretale = () => {
  model.show = true;
};

</script>

<style scoped>

.popout {
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px;
  padding: 1rem;
  margin-top: 1rem;
  margin-bottom: 1rem;
  min-height: 5rem;
}

ol{
  list-style-type: none;
  padding: 0;
}

.person-title{
  font-weight: bold;
}


</style>
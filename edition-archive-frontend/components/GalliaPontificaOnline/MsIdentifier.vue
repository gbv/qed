<template>
  <span class="popout-wrapper position-relative d-inline">
    <a href="#" v-on:click.prevent="model.show ? hideHandschrift():showHandschrift()">
      <i class="bi bi-book"></i>
    </a>
    <slot/>
    <div v-if="model.show" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hideHandschrift()"><i class="bi bi-x-circle"></i></a>
      <div class="link-list">
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/handschriften?handschriftenKey=${props.handschriftenKey}`">{{$t('search.label')}}</nuxt-link>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/handschriften/#${props.handschriftenKey}`">{{$t('gpo.pages.manuscriptIndex')}}</nuxt-link>
      </div>
    </div>
  </span>
</template>
<script lang="ts" setup>

const props = defineProps<{
  handschriftenKey: string
}>();


const route = useRoute();
const model = reactive({ show: false });

const hideHandschrift = () => {
  model.show = false;
};

const showHandschrift = () => {
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



</style>
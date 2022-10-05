<template>
  <div :class="'card h-100 ' + cardClass">
    <nuxt-picture :src="imgHref" :imgAttrs="{ class: 'card-img-top fixed-height', alt:imgAlt }" />
    <div class="card-body">
      <h5 class="card-title mb-5">{{ cardTitle }}</h5>
      <div class="card-text"><slot /></div>
    </div>
    <div class="card-footer text-center no-back mt-3 mb-3" v-if="cardClass == 'active'">
      <nuxt-link :href="link" class="btn btn-secondary text-white stretched-link">{{$t("to_project")}}</nuxt-link>
    </div>
    <div class="card-footer text-center no-back mt-3 mb-3" v-else>
      coming soon
    </div>
  </div>
</template>

<script>
export default {
  props: [
    "imgHref",
    "imgAlt",
    "cardTitle",
    "link",
    "linkText",
    "cardClass"
  ]
}
</script>

<style scoped>

</style>
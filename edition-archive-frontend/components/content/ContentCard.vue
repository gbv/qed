<template>
  <div :class="'card h-100 ' + cardClass">
    <div class="cc-by">
      <nuxt-picture :src="imgHref" loading="lazy" :imgAttrs="{ class: 'cc-by__image card-img-top fixed-height', alt:imgAlt }" />
      <div v-if="ccAuthor" class="cc-by__text" >
        <nuxt-link :href="ccSource" class="no-external-mark">{{$t("cc.photo")}}</nuxt-link>
        {{$t("cc.by")}} {{ccAuthor}} /
        <nuxt-link :href="ccLicenceLink" class="no-external-mark">{{ccLicenceText}}</nuxt-link>
      </div>
    </div>
    <div class="card-body">
      <h5 class="card-title mb-5">{{ cardTitle }}</h5>
      <div class="card-text"><slot /></div>
    </div>
    <div class="card-footer text-center no-back mt-3 mb-3" v-if="cardClass == 'active'">
      <nuxt-link :href="link" class="btn btn-secondary text-white">{{$t("button.toProject")}}</nuxt-link>
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
    "cardClass",
    "ccAuthor",
    "ccSource",
    "ccLicenceText",
    "ccLicenceLink"
  ]
}
</script>

<style scoped>

</style>
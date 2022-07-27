<template>
  <div class="card h-100">
    <img :src="imgHref" class="card-img-top fixed-height" :alt="imgAlt">
    <div class="card-body">
      <h5 class="card-title mb-5">{{ cardTitle }}</h5>
      <p class="card-text"><slot /></p>
    </div>
    <div class="card-footer text-center no-back mt-3 mb-3">
      <a :href="link" class="btn btn-primary text-white stretched-link">{{ linkText }}</a>
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
    "linkText"
  ]
}
</script>

<style scoped>

</style>
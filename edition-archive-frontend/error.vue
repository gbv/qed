<template>
  <NuxtLayout>
    <div class="error-info-hidden">
      <p>
        error.statusCode: {{ error?.statusCode || ""}} <br/>
        error.message: {{ error?.message }}
      </p>
      <hr/>

    </div>
    <!-- customise 404 message from template section -->

    <div class="wrapper">
      <section class="error 404" v-if="error?.statusCode === 404">
        <h3>{{ $t("error.message.404") }}</h3>
        <button class="btn btn-primary float-end" @click="handleError">{{ $t("button.back") }}</button>
      </section>
      <section class="error" v-else>
        <h3>{{ $t("error.message.unknown") }}</h3>
      </section>
    </div>


  </NuxtLayout>
</template>

<script setup lang="ts">
// default props available on error.vue
const props = defineProps({
  error: Object,
});

// customise 404 message from script section
const error = useError();
const route = useRoute();

// clear error and redirect to home page
const handleError = () => {
  const path = route.path;
  const urlParts = path.split("/");
  const redirect = urlParts.length < 2 ? "/" : "/" + urlParts[0];
  //console.log(urlParts);
  //console.log("TO: " + redirect);
  clearError({redirect: redirect});
}
</script>

<style scoped>

.error-info-hidden {
  display: none;
}

.error {
  text-align: center;
}

.wrapper {
}
</style>
<template>
  <div>

    <div class="row">
      <div class="col">
        <h2 class="mb-5">Gallia Pontificia Online</h2>
      </div>
    </div>
    <div class="row">
      <div class="col-12" v-if="data">
        {{ data }}

        Aussteller: {{data.issuer}}
      </div>
    </div>
  </div>

</template>
<script>
export default {}
</script>
<script setup>
const route = useRoute()
const config = useRuntimeConfig()
import {createError} from 'h3'

console.log();
const regestedIdno = route.params.regest;

const {$solrURL, $backendURL} = useNuxtApp();

const {data, error} = await useAsyncData(`idno:${regestedIdno}`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=idno:${regestedIdno}&wt=json`)
  const json = await request.json();
  if (json.response.numFound === 0) {
    throw 404;
  }
  return json.response.docs[0];
})

if (error.value) {
  if (error.value === 404) {
    throwError(
        createError({
          statusCode: 404,
          statusMessage: 'Not Found',
        })
    );
  }
}

</script>
<style scoped>

</style>
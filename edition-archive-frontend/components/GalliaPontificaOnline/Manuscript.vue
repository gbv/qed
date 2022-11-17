<template>
  <div>
    <nuxt-link
      :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/handschriften?handschriftenKey=${$props.manuscript['identifier.key']}`">
      <p v-if="$props.manuscript['shelfmark']" class="shelfmark">{{ $props.manuscript['shelfmark']?.join() }}</p>
    </nuxt-link>
    <div class="link-list">
      <a v-for="key in Object.keys($props.manuscript).filter(str=> str.indexOf('url.') !== -1).sort()"
         :href="$props.manuscript[key]">{{ $t('url_type_' + key.substring('url.'.length).toLowerCase()) }}</a>
    </div>
  </div>
</template>

<script lang="ts" setup>
const route = useRoute()
const props = defineProps(["manuscript"])


</script>

<style scoped>

 .link-list a {
   margin-left: 1em;
  }

</style>
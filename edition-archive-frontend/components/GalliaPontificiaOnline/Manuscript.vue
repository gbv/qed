<template>
  <div>
    <span v-if="$props.manuscript['shelfmark']" class="shelfmark">{{ $props.manuscript['shelfmark']?.join() }}</span>
    <div class="link-list">
      <nuxt-link
        :to="`/gallia-pontificia-online/regesten/${route.params.regesten}/regest/suche/handschriften?handschriftenKey=${$props.manuscript['identifier.key']}`">{{ $t('search.label') }}</nuxt-link>

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
.shelfmark {
  font-weight: bold;
}
</style>
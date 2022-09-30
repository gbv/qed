<template>
  <div>
    <nuxt-link
      :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/quellen?quellenKey=${$props.source['identifier.key']}`">
      {{ $props.source["shortTitle"]?.join() }}
    </nuxt-link>
    <p v-if="$props.source['longTitle']" class="longTitle">{{ $props.source['longTitle']?.join() }}</p>
    <p v-if="$props.source['editionShortTitle']" class="editionShort">{{ $t("edition_short_title") }}:
      {{ $props.source['editionShortTitle']?.join() }}</p>
    <div class="link-list">
      <a v-for="key in Object.keys($props.source).filter(str=> str.indexOf('url.') !== -1).sort()"
         :href="$props.source[key]">{{ $t('url_type_' + key.substring('url.'.length).toLowerCase()) }}</a>
    </div>
  </div>
</template>

<script lang="ts" setup>
const route = useRoute()
const props = defineProps(["source"])
</script>

<style scoped>

</style>
<template>
  <div>
    <span class="source-title">{{ $props.source["shortTitle"] }}</span>
    <p v-if="$props.source['longTitle']" class="longTitle">{{ $props.source['longTitle'] }}</p>
    <p v-if="$props.source['editionShortTitle']" class="editionShort">{{ $t("gpo.editionShortTitle") }}:
    {{ $props.source['editionShortTitle'] }}</p>
    <div class="link-list">
      <nuxt-link :to="`/gallia-pontificia-online/regesten/${route.params.regesten}/regest/suche/quellen?quellenKey=${$props.source['identifier.key']}`">{{$t('search.label')}}</nuxt-link>
      <nuxt-link v-if="$props.hideIndexLink !== true" :to="`/gallia-pontificia-online/regesten/${route.params.regesten}/regest/quellen/#${$props.source['identifier.key']}`">{{$t('gpo.pages.sourceIndex')}}</nuxt-link>
      <a v-for="key in Object.keys($props.source).filter(str=> str.indexOf('url.') !== -1).sort(sortCompare)"
         :href="$props.source[key]">{{ $t('url_type_' + key.substring('url.'.length).toLowerCase()) }}</a>
    </div>
  </div>
</template>

<script lang="ts" setup>
const route = useRoute()
const props = defineProps(["source", "hideIndexLink"])

const sortRegexp = /url\.Band_([IVXLCDM]+),?([0-9]+)?/

const romanToNumber = (roman: string) => {
  const romanToNumberMap: Record<string, number> = {
    I: 1,
    V: 5,
    X: 10,
    L: 50,
    C: 100,
    D: 500,
    M: 1000
  }
  let result = 0
  for (let i = 0; i < roman.length; i++) {
    const current = romanToNumberMap[roman[i]]
    const next = romanToNumberMap[roman[i + 1]]
    if (current < next) {
      result -= current
    } else {
      result += current
    }
  }
  return result

}

const sortCompare = (a: string, b: string) => {
  const aMatch = a.match(sortRegexp)
  const bMatch = b.match(sortRegexp)
  if (aMatch && bMatch) {
    const aRoman = romanToNumber(aMatch[1]);
    const bRoman = romanToNumber(bMatch[1]);

    if(aRoman === bRoman) {
      if(!aMatch[2] || !bMatch[2]) return 0;
      if(aMatch[2] === bMatch[2]) return 0;
      if(!aMatch[2]) return 1;
      if(!bMatch[2]) return -1;
      const aNumber = parseInt(aMatch[2])
      const bNumber = parseInt(bMatch[2])
      return aNumber - bNumber
    } else {
      return aRoman - bRoman
    }
  } else {
    return a.localeCompare(b)
  }
}

</script>

<style scoped>

.source-title {
  font-weight: bold;
}

</style>
<template>
  <div v-for="(label, lang, i) in props.langMap">
    <div class="row">
      <div class="col-12 col-sm-4 col-md-3">
          <span class="label">
            <span class="label-main" v-if="i == 0">{{$t( props.translationKey.toString() ) }}</span>
            <span class="label-lang ml-1" v-if="lang != 'zxx'">
              {{ $t("language." + lang) || lang }}
            </span>
          </span>
      </div>
      <div class="col-12 col-sm-8 col-md-9">
          <span class="value" v-if="!Array.isArray(label)">
            {{ label }}
          </span>
        <span class="value" v-else>
            {{ label.join(", ") }}
          </span>
      </div>
    </div>
  </div>
</template>


<script setup lang="ts">

import type {LangMap} from "~/api/jskos";

const props = defineProps<{
  langMap: LangMap,
  translationKey: string,
}>();


</script>


<style scoped>
.label-main {
  font-weight: bold;
}

.label-lang {
  font-style: italic;
  color: gray;
}

.label-lang:before {
  content: " (";
}

.label-lang:after {
  content: ")";
}

.value {
  word-break: break-word;
}
</style>
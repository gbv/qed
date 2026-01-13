<template>
  <div class="row">
    <div class="col-12 col-sm-4 col-md-3">
          <span class="label">
            <span class="label-main">{{ $t(labelTranslationKey) }}</span>
            <span class="label-lang ml-1" v-if="lang && lang != 'zxx'">
              {{ $t("language." + lang) || lang }}
            </span>
          </span>
    </div>
    <div class="col-12 col-sm-8 col-md-9">
      <span class="value">
        <slot>{{ value }}</slot>
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  labelTranslationKey: string,
  lang?: string,
  value?: string,
}>();

</script>

<style scoped>

</style>
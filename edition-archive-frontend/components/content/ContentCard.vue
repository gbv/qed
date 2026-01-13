<template>
  <div :class="'card h-100 ' + cardClass">
    <div class="cc-by">
      <nuxt-picture :src="imgHref" loading="lazy" :imgAttrs="{ class: 'cc-by__image card-img-top fixed-height', alt:imgAlt }" />
      <div class="badge--coming-soon" v-if="cardClass == 'not-active'">
        coming soon
      </div>
      <div class="badge--old-project" v-if="cardClass == 'old-project'">
        Migration geplant
      </div>
      <div v-if="ccAuthor" class="cc-by__text" >
        <nuxt-link v-if="ccSource" :href="ccSource" class="no-external-mark">{{$t("cc.photo")}}</nuxt-link>
        {{$t("cc.by")}} {{ccAuthor}} /
        <nuxt-link v-if="ccLicenceLink && ccLicenceText" :href="ccLicenceLink" class="no-external-mark">{{ccLicenceText}}</nuxt-link>
      </div>
    </div>
    <div class="card-body">
      <h5 class="card-title mb-5">
        <template v-if="$slots.title">
          <slot name="title" />
        </template>
        <template v-else>
          {{ cardTitle }}
        </template>
      </h5>
      <div class="card-text"><slot /></div>
    </div>
    <div class="card-footer no-back mt-3 mb-3" v-if="cardClass == 'active'">
      <nuxt-link v-if="link" :href="link" class="btn btn-primary text-white no-external-mark">{{$t("button.toProject")}}</nuxt-link>
    </div>
    <div class="card-footer no-back mt-3 mb-3" v-if="cardClass == 'old-project'">
      <nuxt-link v-if="link" :href="link" class="btn btn-primary text-white no-external-mark">Quellen und Datenbanken bis 2018</nuxt-link>
    </div>
  </div>
</template>

<script lang="ts" setup>

const props = defineProps<{
  imgHref: string;
  imgAlt?: string;
  cardTitle?: string;
  link?: string;
  cardClass: string;
  ccAuthor?: string;
  ccSource?: string;
  ccLicenceText?: string;
  ccLicenceLink?: string;
}>();


const slots = defineSlots<{
  default(): any,
  title?(): any
}>();
</script>

<style scoped>

</style>
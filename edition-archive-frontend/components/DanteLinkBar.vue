<template>
  <div class="dante-link-bar" v-if="identifierLinks.length > 0 || danteUri || searchLink || indexLink">
    <a v-if="danteUri" :href="danteUri" target="_blank" rel="noopener noreferrer">DANTE</a>
    <template v-for="id in identifierLinks" :key="id.href">
      <a :href="id.href" target="_blank" rel="noopener noreferrer">{{ id.label }}</a>
    </template>
    <nuxt-link v-if="searchLink" :to="searchLink">{{ $t('search.label') }}</nuxt-link>
    <nuxt-link v-if="indexLink && indexTranslationKey" :to="indexLink">{{ $t(indexTranslationKey) }}</nuxt-link>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  danteUri?: string;
  identifierLinks: { href: string; label: string }[];
  searchLink?: string;
  indexLink?: string;
  indexTranslationKey?: string;
}>();
</script>

<style scoped>
.dante-link-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.dante-link-bar a {
  padding-left: 0.5rem;
  border-left: 1px solid rgba(0, 0, 0, 0.125);
}

.dante-link-bar a:first-child {
  padding-left: 0;
  border-left: none;
}
</style>

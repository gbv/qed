<template>
  <div>
    <span v-if="resolved" class="entity-title">
      {{ preferredLabel }}
    </span>
    <DanteLinkBar
      v-if="resolved"
      :dante-uri="props.refAttribute"
      :identifier-links="identifierLinks"
      :search-link="searchLink"
      :index-link="indexLink"
      :index-translation-key="indexTranslationKey"
    />
    <div class="text-center" v-else-if="loading">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  refAttribute: string;
  entityType: 'person' | 'organisation';
}>();

const danteUri = computed(() => props.refAttribute);
const entityTypeRef = computed(() => props.entityType);

const {preferredLabel, identifierLinks, searchLink, indexLink, indexTranslationKey, loading, resolved} =
  useDanteEntity(danteUri, entityTypeRef);

</script>

<style scoped>
.entity-title {
  font-weight: bold;
  display: block;
  margin-bottom: 0.25rem;
}
</style>

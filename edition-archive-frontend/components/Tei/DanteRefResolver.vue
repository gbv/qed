<template>
  <div>
    <span v-if="resolved" class="entity-title">
      {{ preferredLabel }}
    </span>
    <p v-if="resolved && shownDefinition" class="entity-definition">
      {{ shownDefinition }}
    </p>
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
  entityType?: 'person' | 'organisation';
  preferLongDefinition?: boolean;
}>();

const danteUri = computed(() => props.refAttribute);
const entityTypeRef = computed(() => props.entityType);

const {preferredLabel, firstDefinition, longerDefinition, identifierLinks, searchLink, indexLink, indexTranslationKey, loading, resolved} =
  useDanteEntity(danteUri, entityTypeRef);

const shownDefinition = computed(() => props.preferLongDefinition ? longerDefinition.value : firstDefinition.value);

</script>

<style scoped>
.entity-title {
  font-weight: bold;
  display: block;
  margin-bottom: 0.25rem;
}

.entity-definition {
  margin-bottom: 0.25rem;
}
</style>

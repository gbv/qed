<template>
  <span class="entity-filter-chip">
    <i :class="`bi bi-${icon}`" aria-hidden="true"></i>
    <span class="entity-filter-chip__label">{{ label }}</span>
    <button
      type="button"
      class="entity-filter-chip__remove"
      :title="$t('search.removeFilter')"
      :aria-label="$t('search.removeFilter')"
      v-on:click="$emit('remove')">
      <i class="bi bi-x" aria-hidden="true"></i>
    </button>
  </span>
</template>

<script setup lang="ts">
const props = defineProps<{
  uri: string;
  type: 'person' | 'organisation';
}>();

defineEmits<{
  remove: [];
}>();

const uriRef = toRef(props, 'uri');
const {preferredLabel, loading} = useDanteEntity(uriRef);

const icon = computed(() => props.type === 'organisation' ? 'building' : 'person');

const label = computed(() => {
  if (preferredLabel.value) return preferredLabel.value;
  if (loading.value) return props.uri.split('/').pop() ?? props.uri;
  return props.uri.split('/').pop() ?? props.uri;
});
</script>

<style scoped>
.entity-filter-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.25rem 0.6rem;
  border: 1px solid rgba(0, 0, 0, 0.125);
  border-radius: 1rem;
  background-color: #f1f1f1;
}

.entity-filter-chip__label {
  font-weight: bold;
}

.entity-filter-chip__remove {
  border: none;
  background: none;
  padding: 0;
  line-height: 1;
  cursor: pointer;
  color: inherit;
}
</style>

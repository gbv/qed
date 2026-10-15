<template>
  <span class="keyword-filter-chip">
    <i class="bi bi-tag" aria-hidden="true"></i>
    <span class="keyword-filter-chip__label">{{ label }}</span>
    <button
      type="button"
      class="keyword-filter-chip__remove"
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
}>();

defineEmits<{
  remove: [];
}>();

const uriRef = toRef(props, 'uri');
const {preferredLabel} = useDanteEntity(uriRef);

const label = computed(() => {
  if (preferredLabel.value) return preferredLabel.value;
  return props.uri.split('/').pop() ?? props.uri;
});
</script>

<style scoped>
.keyword-filter-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.25rem 0.6rem;
  border: 1px solid rgba(0, 0, 0, 0.125);
  border-radius: 1rem;
  background-color: #f1f1f1;
}

.keyword-filter-chip__label {
  font-weight: bold;
}

.keyword-filter-chip__remove {
  border: none;
  background: none;
  padding: 0;
  line-height: 1;
  cursor: pointer;
  color: inherit;
}
</style>

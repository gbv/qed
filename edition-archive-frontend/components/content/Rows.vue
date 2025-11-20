<template>
  <div class="row">
    <div v-for="(content, slot) in orderedSlots" :class="`col-${12/Object.keys($slots).length}`">
      <slot :key="slot" :name="slot"/>
    </div>


  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  order?: Array<string>,
}>();

const slots = useSlots();

const orderedSlots = computed(() => {
  if(!props.order) {
    return slots;
  }

  const ordered: Record<string, any> = {};
  props.order.forEach((slotName) => {
    if(slots[slotName]) {
      ordered[slotName] = slots[slotName];
    }
  });

  return ordered;
});

</script>

<style scoped>

</style>
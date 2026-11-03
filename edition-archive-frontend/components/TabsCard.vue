<template>
  <div :class="cardClass">
    <ul class="nav nav-tabs" role="tablist">
      <li v-for="tab in tabs" :key="tab.id" class="nav-item" role="presentation">
        <button
             :class="['nav-link', {active: tab.id === model.currentTab?.id}]"
             :aria-selected="tab.id === model.currentTab?.id"
             type="button"
             role="tab"
             v-on:click="changeToTab(tab)">
             {{ tab.title }}
        </button>
      </li>
    </ul>
    <div class="tab-content p-3" v-if="model.currentTab">
      <slot :name="model.currentTab.id"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps(['tabs', 'current', 'cardClass']);

interface Tab {
  id: string;
  title?: string;
  noTitle?: boolean
}

const emit = defineEmits(['tabChanged']);
const model = reactive({
  currentTab: props.tabs.filter((tab:Tab) => tab.id === props.current)[0]
});

watch(() => props.current, (newCurrent) => model.currentTab = props.tabs.filter((tab:Tab) => tab.id === newCurrent)[0]);

function changeToTab(tab: Tab) {
  const old = model.currentTab;
  model.currentTab = tab;
  emit('tabChanged', {old: old, _new: model.currentTab});
}

</script>

<style scoped>

</style>
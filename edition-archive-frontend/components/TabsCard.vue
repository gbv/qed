<template>
  <div class="card" :class="cardClass">
    <div class="card-header">
      <ul class="nav nav-tabs card-header-tabs">
        <li v-for="tab in tabs" class="nav-item">
          <a class="nav-link"
             :class="tab === model.currentTab ? 'active' : ''"
             v-on:click="changeToTab(tab)"
             href="#">
             <span v-if="tab.id == 'einfach'">
              {{ $t("search.basic")}}
             </span>
             <span v-else>
              {{ $t("search.extended")}}
             </span>
          </a>
        </li>
      </ul>
    </div>
    <div class="card-body mt-3" v-if="model.currentTab">
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
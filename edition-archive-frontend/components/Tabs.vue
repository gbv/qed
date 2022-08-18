<template>
  <div class="card" :class="cardClass">
    <div class="card-header">
      <ul class="nav nav-tabs card-header-tabs">
        <li v-for="tab in tabs" class="nav-item">
          <a class="nav-link"
             :class="tab === currentTab ? 'active' : ''"
             v-on:click="changeToTab(tab)"
             href="#">{{ tab.title }}</a>
        </li>
      </ul>
    </div>
    <div class="card-body">
      <h5 v-if="!currentTab.noTitle" class="card-title">{{ currentTab.title }}</h5>
      <slot :name="currentTab.id"></slot>
    </div>
  </div>
</template>

<script setup>
const {tabs, current, cardClass} = defineProps(['tabs', 'current', 'cardClass']);
const currentTab = ref(tabs.filter((tab) => tab.id === current)[0]);
const emit = defineEmits(['tabChanged']);

function changeToTab(tab) {
  const old = currentTab.value;
  currentTab.value = tab;
  emit('tabChanged', {old: old, _new: currentTab.value});
}

</script>

<style scoped>

</style>
<template>
  <div class="browse">
    <span class="prev">
      <a href="#prev" v-if="prevLabel" v-on:click.prevent="prevClicked">
        <i class="bi bi-chevron-left"></i>
        {{ prevLabel }}
      </a>
    </span>
    <span class="index">
      {{ $t("browse_current_of", {current, of}) }}
    </span>
    <span class="next">
      <a href="#next"  v-if="nextLabel" v-on:click.prevent="nextClicked">
        {{ nextLabel }}
        <i class="bi bi-chevron-right"></i>
      </a>
    </span>
  </div>
</template>

<script lang="ts" setup>
const props = defineProps<{ prevLabel?: string, nextLabel?: string, current: number, of: number }>()

const emit = defineEmits(["prevClicked", "nextClicked"]);

const prevClicked = () => {
  emit("prevClicked");
}

const nextClicked = () => {
  emit("nextClicked");
}

</script>

<style scoped>
.browse {
  flex-direction: row;
  display: flex;
  align-items: stretch;
  flex-grow: 1;
}

.prev {
  flex: 1;
  text-align: left;
}

.index {
  flex: 1;
  text-align: center;
}

.next {
  flex: 1;
  text-align: right;
}

</style>
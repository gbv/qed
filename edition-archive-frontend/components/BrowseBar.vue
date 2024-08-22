<template>
  <div class="browse">
    <span class="prev">
      <a href="#prev" v-if="prevLabel" v-on:click.prevent="prevClicked">
        <i class="bi bi-chevron-left"></i>
        {{ prevLabel }}
      </a>
    </span>
    <div class="regest-index">
      <div class="regest-index__current">
        <input
          class="form-control form-control-sm"
          :class="model.index < 1 || model.index > of ? 'is-invalid' : ''"
          v-model.number="model.index"
          :title="$t('browser.changeRegest')"
          v-on:keyup.enter="indexEntered" />
        <div class="invalid-feedback" v-if="model.index < 1 || model.index > of">
          {{ $t("browser.invalidIndex") }}
        </div>
      </div>
      <div class="regest-index__overall">
        {{ $t("browser.currentOf") }}
        {{ of }}
      </div>
      <div class="regest-index__goto" v-if="model.index != current && model.index > 1 && model.index < of">
        <i
          class="bi bi-arrow-clockwise clickable"
          v-on:click="indexEntered"
          :title="$t('browser.goTo', {to:model.index})"></i>
      </div>
    </div>
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

const emit = defineEmits(["prevClicked", "nextClicked", "indexEntered"]);

const model = reactive({editIndex: false, index: props.current});


const prevClicked = () => {
  emit("prevClicked");
}

const nextClicked = () => {
  emit("nextClicked");
}

const indexEntered = () => {
  if(model.index < 1 || model.index > props.of) {
    return;
  }
  model.editIndex=false;
  emit("indexEntered", model.index);
}

</script>

<style scoped>
.clickable {
  cursor: pointer;
}

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

.regest-index {
  flex: 1;
  text-align: center;
}

.next {
  flex: 1;
  text-align: right;
}

</style>
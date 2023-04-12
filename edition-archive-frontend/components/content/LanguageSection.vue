<template>
   <div class="row">
      <div class="col-12">
        <div v-for="(slot, name) in $slots" :class="currentSlot!==name ? 'd-none' : ''">
          <slot :name="name"/>
        </div>
      </div>
    </div>
</template>

<script lang="ts" setup>
import {useI18n} from "vue-i18n";
import {useSlots} from "@vue/runtime-core";

const props = defineProps(['fallback', "changer"]);
const slots = useSlots();
const i18n = useI18n();
const currentLanguage = ref(i18n.locale.value)
const currentSlot = ref(calcCurrentSlot());

function calcCurrentSlot(): string {
  return currentLanguage.value in slots ? currentLanguage.value : props.fallback;
}

watch(() => currentLanguage.value, (_new, old) => {
  currentSlot.value = calcCurrentSlot();
})

watch(() => i18n.locale.value, (_new, old) => {
  //console.log(["Watch", _new])
  currentLanguage.value = _new;

});

</script>

<style scoped>

</style>
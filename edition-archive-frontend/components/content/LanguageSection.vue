<template>
  <client-only>
    <section v-if="props.changer" class="row">
      <div class="col-2 offset-10">
        <div class="dropdown">
          <a id="dropdownMenuLink" aria-expanded="false" class="btn btn-sm btn-primary dropdown-toggle"
             data-bs-toggle="dropdown"
             href="#" role="button" v-on:click.prevent="">
            <i class="bi bi-translate"></i>
          </a>
          <ul aria-labelledby="dropdownMenuLink" class="dropdown-menu">
            <li v-for="(content, name) in $slots">
              <a class="dropdown-item" href="#" v-on:click.prevent="changeLanguage(name)">
                <LanguageDisplay :lang="name"/>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </section>
    <div class="row">
      <div class="col-12">
        <div v-for="(slot, name) in $slots" :class="currentSlot!==name ? 'd-none' : ''">
          <slot :name="name"/>
        </div>
      </div>
    </div>
  </client-only>
</template>

<script lang="ts" setup>
import {useI18n} from "vue-i18n";
import {useSlots} from "@vue/runtime-core";

const props = defineProps(['fallback', "changer"]);
const slots = useSlots();
const i18n = useI18n();
const currentLanguage = ref(i18n.locale.value)
const currentSlot = ref(calcCurrentSlot());

function changeLanguage(slot: string | number) {
  if (typeof slot == "string") {
    currentLanguage.value = slot;
  }
}

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
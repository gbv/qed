<template>
  <div>
    <span v-if="$props.name.displayForm != null">
      {{ $props.name.displayForm }}
    </span>
    <span v-else>
      {{ $props.name.nameParts.join(", ") }}
    </span>
    <span :class="`bi bi-${icon}`" v-on:click="model.show=!model.show"> </span>


    <div v-if="model.show && (namePartGiven || namePartFamily || date || affiliation)" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="model.show = false"><i class="bi bi-x-circle"></i></a>

        <div class="row termsOfAddress" v-if="termsOfAddress">
          <div class="col-4">{{ $t("sosu.metadata.name.termsOfAddress") }}</div>
          <div class="col-8">{{ termsOfAddress }}</div>
        </div>

        <div class="row given" v-if="namePartGiven">
          <div class="col-4">{{ $t("sosu.metadata.name.given") }}</div>
          <div class="col-8">{{ namePartGiven }}</div>
        </div>

        <div class="row family" v-if="namePartFamily">
          <div class="col-4">{{ $t("sosu.metadata.name.family") }}</div>
          <div class="col-8">{{ namePartFamily }}</div>
        </div>

        <div class="row date" v-if="date">
          <div class="col-4">{{ $t("sosu.metadata.name.date") }}</div>
          <div class="col-8">{{ date }}</div>
        </div>

        <div class="row affiliation" v-if="affiliation">
          <div class="col-4">{{ $t("sosu.metadata.name.affiliation") }}</div>
          <div class="col-8">{{ affiliation }}</div>
        </div>

    </div>

  </div>
</template>

<script setup lang="ts">

import {Name, NamePart} from "~/api/Mods";

const props = defineProps<{
  name: Name,
  role?: string
}>()

const model = reactive({
  show: false,
})

const affiliation = computed(()=> {
  return props.name.affiliation;
})

const role = computed(() => {
  return props.role != null ? props.role : props.name.roles[0];
});

const icon = computed(() => {
  if(props.name.type === "personal") {
    return "person";
  } else if(props.name.type === "corporate") {
    return "building";
  } else {
    return "question-circle";
  }
});

const date = computed(() => {
  return props.name.nameParts
    .filter((namePart: NamePart) => namePart.type === "date")
    .map((namePart: NamePart) => namePart.value)
    .join(", ");
});

const namePartFamily = computed(() => {
  return props.name.nameParts
    .filter((namePart: NamePart) => namePart.type === "family")
    .map((namePart: NamePart) => namePart.value)
    .join(", ");
});

const namePartGiven = computed(() => {
  return props.name.nameParts
    .filter((namePart: NamePart) => namePart.type === "given")
    .map((namePart: NamePart) => namePart.value)
    .join(", ");
});


const termsOfAddress = computed(() => {
  return props.name.nameParts
    .filter((namePart: NamePart) => namePart.type === "termsOfAddress")
    .map((namePart: NamePart) => namePart.value)
    .join(", ");
});


</script>

<style scoped>

.popout {
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px;
  padding: 1rem;
  margin-top: 1rem;
  margin-bottom: 1rem;
  min-height: 5rem;
}
</style>
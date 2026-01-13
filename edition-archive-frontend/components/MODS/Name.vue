<template>
  <div>
    <a href="#"
      v-if="expandable"
      :class="`bi bi-${icon} bi-interactive`"
      v-on:click.prevent="model.show=!model.show"> </a>
    <span
      v-else
      :class="`bi bi-${icon}`"
      v-on:click.prevent="model.show=!model.show" > </span>

    <span v-if="props.name.displayForm != null">
      <!-- display form code -->
      {{ props.name.displayForm }}
    </span>
    <span v-else-if="props.name.nameParts != null && props.name.nameParts.length > 0">
      <!-- name part code -->
      {{ props.name.nameParts.join(", ") }}
    </span>
    <span v-else-if="props.name.classification && props.name.category  && props.appUrl">
      <!-- classification code -->
      <MODSClassification :appUrl="props.appUrl"
                          :classId="props.name.classification"
                          :categ-id="props.name.category" />
    </span>

    <div v-if="model.show && expandable" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="model.show = false"><i class="bi bi-x-circle"></i></a>

        <div class="row termsOfAddress" v-if="termsOfAddress">
          <div class="col-4">{{ $t("metadata.name.termsOfAddress") }}</div>
          <div class="col-8">{{ termsOfAddress }}</div>
        </div>

        <div class="row given" v-if="namePartGiven">
          <div class="col-4">{{ $t("metadata.name.given") }}</div>
          <div class="col-8">{{ namePartGiven }}</div>
        </div>

        <div class="row family" v-if="namePartFamily">
          <div class="col-4">{{ $t("metadata.name.family") }}</div>
          <div class="col-8">{{ namePartFamily }}</div>
        </div>

        <div class="row date" v-if="date">
          <div class="col-4">{{ $t("metadata.name.date") }}</div>
          <div class="col-8">{{ date }}</div>
        </div>

        <div class="row affiliation" v-if="affiliation">
          <div class="col-4">{{ $t("metadata.name.affiliation") }}</div>
          <div class="col-8">{{ affiliation }}</div>
        </div>

      <div class="row name-identifiers" v-for="identifier in props.name.nameIdentifiers">
        <div class="col-4 identifier-type">{{ identifier.type }}</div>
        <div class="col-8 identifier-value">
          <a v-if="identifier.typeURI != null" :href="`${identifier.typeURI}${identifier.value}`"
             target="_blank" rel="noopener">
            {{ identifier.value }}
          </a>
          <template v-else>{{ identifier.value }}</template>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup lang="ts">

import {type Name, type NamePart} from "~/api/Mods";

const props = defineProps<{
  name: Name,
  role?: string,
  appUrl?: string
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

const expandable = computed(() => namePartGiven.value || namePartFamily.value || date.value || affiliation.value || (props.name.nameIdentifiers && props.name.nameIdentifiers.length > 0));

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
  if (!props.name.nameParts) {
    return undefined;
  }
  return props.name.nameParts
    .filter((namePart: NamePart) => namePart.type === "date")
    .map((namePart: NamePart) => namePart.value)
    .join(", ");
});

const namePartFamily = computed(() => {
  if (!props.name.nameParts) {
    return undefined;
  }
  return props.name.nameParts
    .filter((namePart: NamePart) => namePart.type === "family")
    .map((namePart: NamePart) => namePart.value)
    .join(", ");
});

const namePartGiven = computed(() => {
  if (!props.name.nameParts) {
    return undefined;
  }
  return props.name.nameParts
    .filter((namePart: NamePart) => namePart.type === "given")
    .map((namePart: NamePart) => namePart.value)
    .join(", ");
});


const termsOfAddress = computed(() => {
  if (!props.name.nameParts) {
    return null;
  }
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

.identifier-type {
  text-transform: uppercase;
}
</style>
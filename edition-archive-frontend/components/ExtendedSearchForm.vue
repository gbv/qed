<template>
  <form v-on:submit.prevent="submit">
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchAllMeta">{{ $t("search.field.allMeta") }}</label>
      <div class="col-9">
        <input id="searchAllMeta" type="search" class="form-control" v-model="model.allMeta">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchPerson">{{ $t("search.field.person") }}</label>
      <div class="col-9">
        <input id="searchPerson" type="search" class="form-control" v-model="model.person">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchPlace">{{ $t("search.field.place") }}</label>
      <div class="col-9">
        <input id="searchPlace" type="search" class="form-control" v-model="model.place">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchInitium">{{ $t("search.field.initium") }}</label>
      <div class="col-9">
        <input id="searchInitium" type="search" class="form-control" v-model="model.initium">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchRecipient">{{ $t("search.field.recipient") }}</label>
      <div class="col-9">
        <input id="searchRecipient" type="search" class="form-control" v-model="model.recipient">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchRecipient">{{ $t("search.field.issuer") }}</label>
      <div class="col-9">
        <input id="searchIssuer" type="search" class="form-control" v-model="model.issuer">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="jaffe2">{{ $t("search.field.jaffe2") }}</label>
      <div class="col-9">
        <input id="jaffe2" type="search" class="form-control" v-model="model.jaffe2">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="jaffe3">{{ $t("search.field.jaffe3") }}</label>
      <div class="col-9">
        <input id="jaffe3" type="search" class="form-control" v-model="model.jaffe3">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchDate">{{ $t("search.field.date") }}</label>
      <div class="col-2">
        <div class="form-check mt-2 text-align-left">
          <label class="form-check-label" for="dateRangeLabel">
            {{ $t("search.field.dateRangeLabel") }}
          </label>
          <input class="form-check-input" type="checkbox" v-model="model.dateRangeRange" id="dateRangeLabel">
        </div>
      </div>
      <div class="col-7">
        <div class="input-group" v-if="model.dateRangeRange">
          <input id="searchDate" type="number" class="form-control" v-model="model.dateRangeFrom">
          <span class="input-group-text">-</span>
          <input id="searchDate" type="number" class="form-control" v-model="model.dateRangeTo">
        </div>
        <input v-else id="searchDate" type="number" class="form-control" v-model="model.dateRangeFrom">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchDateText">{{ $t("search.field.dateText") }}</label>
      <div class="col-9">
        <input id="searchDateText" type="search" class="form-control" v-model="model.dateText">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="row">
      <div class="col-12 text-end mt-3">
        <button type="submit" class="btn btn-primary">
          {{ $t("search.submit") }}
          <i class="bi bi-search ms-1"></i>
        </button>
      </div>
    </div>
  </form>
</template>

<script lang="ts" setup>


const emit = defineEmits(["search"])
const props = defineProps(["person", "place", "allMeta", "initium", "issuer", "recipient", "lost", "fake", "certainly", "dateRangeRange", "dateRangeFrom", "dateRangeTo", "dateText", "jaffe2", "jaffe3"])

const model = reactive({
  allMeta: props.allMeta,
  person: props.person,
  place: props.place,
  initium: props.initium,
  issuer: props.issuer,
  recipient: props.recipient,
  lost: props.lost,
  fake: props.fake,
  certainly: props.certainly,
  dateRangeRange: props.dateRangeRange,
  dateRangeFrom: props.dateRangeFrom,
  dateRangeTo: props.dateRangeTo,
  dateText: props.dateText,
  jaffe2: props.jaffe2,
  jaffe3: props.jaffe3
});

watch(() => props.allMeta, (newAllMeta) => {
  model.allMeta = newAllMeta;
});

watch(() => props.person, (newPerson) => {
  model.person = newPerson;
});

watch(() => props.place, (newPlace) => {
  model.place = newPlace;
});

watch(() => props.initium, (newInitium) => {
  model.initium = newInitium;
});

watch(() => props.issuer, (newIssuer) => {
  model.issuer = newIssuer;
});

watch(() => props.recipient, (newRecipient) => {
  model.recipient = newRecipient;
});

watch(() => props.dateRangeRange, (newDateRange) => {
  model.dateRangeRange = newDateRange;
});
watch(() => props.dateRangeFrom, (newDateRangeFrom) => {
  model.dateRangeFrom = newDateRangeFrom;
});

watch(() => props.dateRangeTo, (newDateRangeTo) => {
  model.dateRangeTo = newDateRangeTo;
});

watch(() => props.dateText, (newDateText) => {
  model.dateText = newDateText;
});

watch(() => props.jaffe2, (newJaffe2) => {
  model.jaffe2 = newJaffe2;
});

watch(() => props.jaffe3, (newJaffe3) => {
  model.jaffe3 = newJaffe3;
});

const submit = () => {
  const result = {} as any;
  let m = model as any;
  for (const key in model){
    if(m[key] !== null && m[key] != ""){
      if(key === "dateRangeTo" && !m.dateRangeRange){
        continue;
      }

      result[key] = m[key];
    }
  }

  emit("search", result);
}

</script>

<style scoped>

</style>
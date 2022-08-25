<template>
  <form v-on:submit.prevent="submit">
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchAllMeta">{{ $t("search_field_allMeta") }}</label>
      <div class="col-9">
        <input id="searchAllMeta" type="search" class="form-control" v-model="model.allMeta"
               :placeholder="$t('search_field_allMeta_placeholder')">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchPerson">{{ $t("search_field_person") }}</label>
      <div class="col-9">
        <input id="searchPerson" type="search" class="form-control" v-model="model.person"
               :placeholder="$t('search_field_person_placeholder')">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchPlace">{{ $t("search_field_place") }}</label>
      <div class="col-9">
        <input id="searchPlace" type="search" class="form-control" v-model="model.place"
               :placeholder="$t('search_field_place_placeholder')">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchInitium">{{ $t("search_field_initium") }}</label>
      <div class="col-9">
        <input id="searchInitium" type="search" class="form-control" v-model="model.initium"
               :placeholder="$t('search_field_initium_placeholder')">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchRecipient">{{ $t("search_field_recipient") }}</label>
      <div class="col-9">
        <input id="searchRecipient" type="search" class="form-control" v-model="model.recipient"
               :placeholder="$t('search_field_recipient_placeholder')">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchDate">{{ $t("search_field_date") }}</label>
      <div class="col-2">
        <div class="form-check mt-2 text-align-left">
          <label class="form-check-label" for="dateRangeLabel">
            {{ $t("search_field_date_range_label") }}
          </label>
          <input class="form-check-input" type="checkbox" v-model="model.dateRangeRange" id="dateRangeLabel">
        </div>
      </div>
      <div class="col-7">
        <div class="input-group" v-if="model.dateRangeRange">
          <input id="searchDate" type="date" class="form-control" v-model="model.dateRangeFrom"
                 :placeholder="$t('search_field_date_placeholder')">
          <span class="input-group-text">-</span>
          <input id="searchDate" type="date" class="form-control" v-model="model.dateRangeTo"
                 :placeholder="$t('search_field_date_placeholder')">
        </div>
        <input v-else id="searchDate" type="date" class="form-control" v-model="model.dateRangeFrom"
               :placeholder="$t('search_field_date_placeholder')">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="form-group row">
      <label class="col-sm-2 col-form-label mb-2" for="searchDateText">{{ $t("search_field_dateText") }}</label>
      <div class="col-9">
        <input id="searchDateText" type="search" class="form-control" v-model="model.dateText"
               :placeholder="$t('search_field_dateText_placeholder')">
      </div>
      <div class="col-1">

      </div>
    </div>
    <div class="row">
      <div class="col-12 text-end">
        <button type="submit" class="btn btn-primary">{{ $t("search_submit") }}</button>
      </div>
    </div>
  </form>
</template>

<script lang="ts" setup>


const emit = defineEmits(["search"])
const props = defineProps(["person", "place", "allMeta", "initium", "issuer", "recipient", "lost", "fake", "certainly", "dateRangeRange", "dateRangeFrom", "dateRangeTo", "dateText"]);

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
  dateText: props.dateText
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

const submit = () => {
  const result = {};
  for (const key in model){
    if(model[key] !== null && model[key] != ""){
      result[key] = model[key];
    }
  }

  console.log(result);
  emit("search", result);
}

</script>

<style scoped>

</style>
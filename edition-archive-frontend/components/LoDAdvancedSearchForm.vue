<template>
  <form v-on:submit.prevent="submit">
    <!-- Free text search -->
    <div class="form-group row mb-3">
      <label class="col-sm-2 col-form-label" for="searchAllMeta">{{ $t("search.field.allMeta") }}</label>
      <div class="col-9">
        <input id="searchAllMeta" type="search" class="form-control" v-model="model.allMeta">
      </div>
    </div>

    <!-- Person (with typeahead) -->
    <div class="form-group row mb-3">
      <label class="col-sm-2 col-form-label" for="searchPerson">{{ $t("search.field.person") }}</label>
      <div class="col-9">
        <input id="searchPerson" type="search" class="form-control" v-model="model.person" @input="debouncedPersonSearch">
        <div v-if="personSuggestions.length > 0" class="suggest-dropdown">
          <ul class="list-group">
            <li v-for="suggestion in personSuggestions" :key="suggestion.uri" 
                class="list-group-item list-group-item-action" 
                @click="selectPersonSuggestion(suggestion)">
              <strong>{{ suggestion.label }}</strong>
              <small v-if="suggestion.type" class="text-muted"> ({{ suggestion.type }})</small>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Place (with typeahead) -->
    <div class="form-group row mb-3">
      <label class="col-sm-2 col-form-label" for="searchPlace">{{ $t("search.field.place") }}</label>
      <div class="col-9">
        <input id="searchPlace" type="search" class="form-control" v-model="model.place" @input="debouncedPlaceSearch">
        <div v-if="placeSuggestions.length > 0" class="suggest-dropdown">
          <ul class="list-group">
            <li v-for="suggestion in placeSuggestions" :key="suggestion.uri" 
                class="list-group-item list-group-item-action" 
                @click="selectPlaceSuggestion(suggestion)">
              <strong>{{ suggestion.label }}</strong>
              <small v-if="suggestion.type" class="text-muted"> ({{ suggestion.type }})</small>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Subject matter -->
    <div class="form-group row mb-3">
      <label class="col-sm-2 col-form-label" for="searchSubject">{{ $t("search.field.subject") }}</label>
      <div class="col-9">
        <input id="searchSubject" type="search" class="form-control" v-model="model.subject">
      </div>
    </div>

    <!-- Date and date range -->
    <div class="form-group row mb-3">
      <label class="col-sm-2 col-form-label" for="searchDate">{{ $t("search.field.date") }}</label>
      <div class="col-2">
        <div class="form-check mt-2">
          <label class="form-check-label" for="dateRangeLabel">
            {{ $t("search.field.dateRangeLabel") }}
          </label>
          <input class="form-check-input" type="checkbox" v-model="model.dateRangeRange" id="dateRangeLabel">
        </div>
      </div>
      <div class="col-7">
        <div class="input-group" v-if="model.dateRangeRange">
          <input id="searchDateFrom" type="text" inputmode="numeric" pattern="[0-9]{4}(-[0-9]{2}-[0-9]{2})?" class="form-control" v-model="model.dateRangeFrom" :placeholder="$t('search.field.datePlaceholder')">
          <span class="input-group-text">-</span>
          <input id="searchDateTo" type="text" inputmode="numeric" pattern="[0-9]{4}(-[0-9]{2}-[0-9]{2})?" class="form-control" v-model="model.dateRangeTo" :placeholder="$t('search.field.datePlaceholder')">
        </div>
        <input v-else id="searchDate" type="text" inputmode="numeric" pattern="[0-9]{4}(-[0-9]{2}-[0-9]{2})?" class="form-control" v-model="model.dateRangeFrom" :placeholder="$t('search.field.datePlaceholder')">
      </div>
    </div>

    <!-- Date (Text) -->
    <div class="form-group row mb-3">
      <label class="col-sm-2 col-form-label" for="searchDateText">{{ $t("search.field.dateText") }}</label>
      <div class="col-9">
        <input id="searchDateText" type="search" class="form-control" v-model="model.dateText">
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
import { ref, watch, reactive } from 'vue'

const { locale } = useI18n()

const emit = defineEmits(["search"])
const props = defineProps({
  allMeta: { type: String, default: "" },
  person: { type: String, default: "" },
  place: { type: String, default: "" },
  subject: { type: String, default: "" },
  dateRangeRange: { type: Boolean, default: false },
  dateRangeFrom: { type: [Number, String], default: "" },
  dateRangeTo: { type: [Number, String], default: "" },
  dateText: { type: String, default: "" }
})

const model = reactive({
  allMeta: props.allMeta,
  person: props.person,
  place: props.place,
  subject: props.subject,
  dateRangeRange: props.dateRangeRange,
  dateRangeFrom: props.dateRangeFrom,
  dateRangeTo: props.dateRangeTo,
  dateText: props.dateText
})

// Typeahead suggestions
const personSuggestions = ref<any[]>([])
const placeSuggestions = ref<any[]>([])
const personSearchTimeout = ref<ReturnType<typeof setTimeout> | null>(null)
const placeSearchTimeout = ref<ReturnType<typeof setTimeout> | null>(null)

// Watch props and update model
watch(() => props.allMeta, (newAllMeta) => { model.allMeta = newAllMeta })
watch(() => props.person, (newPerson) => { model.person = newPerson })
watch(() => props.place, (newPlace) => { model.place = newPlace })
watch(() => props.subject, (newSubject) => { model.subject = newSubject })
watch(() => props.dateRangeRange, (newDateRange) => { model.dateRangeRange = newDateRange })
watch(() => props.dateRangeFrom, (newDateRangeFrom) => { model.dateRangeFrom = newDateRangeFrom })
watch(() => props.dateRangeTo, (newDateRangeTo) => { model.dateRangeTo = newDateRangeTo })
watch(() => props.dateText, (newDateText) => { model.dateText = newDateText })

// Debounced search functions
const debouncedPersonSearch = (event: Event) => {
  const value = (event.target as HTMLInputElement).value
  if (value.length < 3) {
    personSuggestions.value = []
    return
  }
  
  if (personSearchTimeout.value) {
    clearTimeout(personSearchTimeout.value)
  }
  
  personSearchTimeout.value = setTimeout(() => {
    searchDante('person', value)
  }, 300)
}

const debouncedPlaceSearch = (event: Event) => {
  const value = (event.target as HTMLInputElement).value
  if (value.length < 3) {
    placeSuggestions.value = []
    return
  }
  
  if (placeSearchTimeout.value) {
    clearTimeout(placeSearchTimeout.value)
  }
  
  placeSearchTimeout.value = setTimeout(() => {
    searchDante('place', value)
  }, 300)
}

// Search DANTE API
const searchDante = async (type: 'person' | 'place', search: string) => {
  try {
    const language = locale.value || 'en'
    const url = `https://api.dante.gbv.de/suggest?search=${encodeURIComponent(search)}&type=${type}&language=${language}&limit=10`
    
    const response = await fetch(url)
    const data = await response.json()
    
    if (type === 'person') {
      personSuggestions.value = data.results || []
    } else {
      placeSuggestions.value = data.results || []
    }
  } catch (error) {
    console.error(`Error searching ${type}:`, error)
    if (type === 'person') {
      personSuggestions.value = []
    } else {
      placeSuggestions.value = []
    }
  }
}

// Select suggestion
const selectPersonSuggestion = (suggestion: any) => {
  model.person = suggestion.label
  personSuggestions.value = []
}

const selectPlaceSuggestion = (suggestion: any) => {
  model.place = suggestion.label
  placeSuggestions.value = []
}

// Clear form
const clear = () => {
  model.allMeta = ""
  model.person = ""
  model.place = ""
  model.subject = ""
  model.dateRangeRange = false
  model.dateRangeFrom = ""
  model.dateRangeTo = ""
  model.dateText = ""
  personSuggestions.value = []
  placeSuggestions.value = []
  emit("search", {})
}

// Submit
const submit = () => {
  const result = {} as any
  const m = model as any
  
  for (const key in model) {
    if (m[key] !== null && m[key] !== "" && m[key] !== undefined) {
      if (key === "dateRangeTo" && !m.dateRangeRange) {
        continue
      }
      result[key] = m[key]
    }
  }
  
  emit("search", result)
}
</script>

<style scoped>
.suggest-dropdown {
  position: absolute;
  z-index: 1000;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  background: white;
  border: 1px solid #ddd;
  border-radius: 0 0 4px 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.list-group-item {
  cursor: pointer;
}

.list-group-item:hover {
  background-color: #f0f0f0;
}
</style>
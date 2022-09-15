<template>
  <span>{{ convert($props.lang) }}</span>
</template>

<script lang="ts" setup>
const props = defineProps(["lang"]);

function convert(langCode){
  switch (langCode){
    case "en":
      return "English";
    case "de":
      return "Deutsch";
    case "fr":
      return "Français";
    default:
      return langCode;
  }
}

</script>

<style scoped>

</style>
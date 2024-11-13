<template>
  <span>{{ convert($props.lang) }}</span>
</template>

<script lang="ts" setup>
const props = defineProps(["lang"]);

function convert(langCode:string){
  switch (langCode){
    case "en":
      return "English";
    case "de":
      return "Deutsch";
    case "fr":
      return "Français";
    case "ru":
      return "Русский";
    case "uk":
      return "Українська";
    default:
      return langCode;
  }
}

</script>

<style scoped>

</style>
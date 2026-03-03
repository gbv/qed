<template>
  <div v-if="translatedLicense"
       class="row citation-display">
    <div class="col">
      {{
        $t(props.translationTemplate, {
          license: translatedLicense,
          authors: authorsStrings.join(", "),
          title: title,
          publicationYear: publicationYearAndPlace?.dateIssued,
          publicationPlace: publicationYearAndPlace?.placeTerm
        })
      }}
      <a v-if="url" :href="url">
        {{ url }}
      </a>
    </div>

    <div class="col-auto regest-licence text-end" v-if="licenceCategory.startsWith('cc_')">
      <a v-if="categoryURL" :href="categoryURL" :title="translatedLicense"
         class="no-external-mark">
        <nuxt-img src="/images/creative-commons.svg" alt="cc"/>
        <nuxt-img v-if="licenceCategory.indexOf('by') != 0" src="/images/creative-commons-by.svg"
                  alt="by"/>
        <nuxt-img v-if="licenceCategory.indexOf('sa') != 0" src="/images/creative-commons-sa.svg"
                  alt="sa"/>
      </a>
    </div>
  </div>
</template>

<style scoped>

.citation-display {
  margin-top: .5rem;
}

img {
  text-align: right;
  margin-bottom: 3rem;
}

</style>

<script setup lang="ts">

import {
  and,
  byAttr,
  byName,
  findFirstElement, flattenElement,
  getAttribute,
  type XElement,
  XMLApi
} from "~/api/XMLApi";

const props = defineProps<{
  mods: XElement,
  licenceAppURL: string,
  licenceClass: string,
  licenceCategory: string,
  translationTemplate: string,
}>();

const i18n = useI18n();

const response = useAsyncData(`clazz-${props.licenceClass}`, async () => {
  const response = await fetch(`${props.licenceAppURL}api/v2/classifications/${props.licenceClass}`);
  return await response.text().then((xmlText) => {
    return XMLApi(xmlText);
  });
});


const categoryElement = computed(() => {
  const classificationElement = response.data.value;

  if (!classificationElement) {
    return undefined;
  }


  return findFirstElement(classificationElement, and(byName("category"),
    byAttr("ID", props.licenceCategory)));
});


const translatedLicense = computed(() => {
  if (categoryElement.value == null) {
    return i18n.t("metadata.unknownLicense");
  }

  let labelElement = findFirstElement(categoryElement.value, and(byName("label"),
    byAttr("xml:lang", i18n.locale.value)));


  if (labelElement == null) {
    labelElement = findFirstElement(categoryElement.value, byName("label"));
  }

  if (labelElement == null) {
    return i18n.t("metadata.unknownLicense");
  }

  return getAttribute(labelElement, "text")?.value;
});


const categoryURL = computed(() => {
  if (categoryElement.value == null) {
    return null;
  }

  const urlElement = findFirstElement(categoryElement.value, byName("url"));

  if (urlElement == null) {
    return null;
  }

  return getAttribute(urlElement, "xlink:href")?.value || null;
});

const relatedItem = computed(() => {
  return findFirstElement(props.mods, and(byName("mods:relatedItem"),
    byAttr("type", "host")));
});

const authorsElements = computed(() => {
  return props.mods.content
    .filter(and(byName("mods:name"), byAttr("type", "personal")))
    .filter((name) => {
      const roleTerm = findFirstElement(name as XElement, and(byName("mods:roleTerm")));
      return roleTerm != null && flattenElement(roleTerm)?.trim() === "aut";
    }) as XElement[];

});

const authorsStrings = computed(() => {
  return authorsElements.value.map((name) => {
    const namePartElements = name.content.filter(byName("mods:namePart"));
    return namePartElements
      .sort((a, b) => {
        const typeA = getAttribute(a as XElement, "type")?.value || "";
        const typeB = getAttribute(b as XElement, "type")?.value || "";
        if (typeA === "family" && typeB !== "family") {
          return -1;
        } else if (typeA !== "family" && typeB === "family") {
          return 1;
        } else {
          return 0;
        }
      })
      .map((namePart) => flattenElement(namePart)).join(" ");
  });
});


const title = computed(() => {
  return props.mods.content.filter(byName("mods:titleInfo")).map((titleInfo) => {
    const titleElement = findFirstElement(titleInfo as XElement, byName("mods:title"));
    if (titleElement) {
      return flattenElement(titleElement);
    }
    return null;
  })[0];
});


const publicationYearAndPlace = computed(() => {
  if(!relatedItem) {
    return null;
  }
  return relatedItem.value?.content
    .filter(byName("mods:originInfo"))
    .filter(byAttr("eventType", "publication"))
    .map((originInfo) => {
    let dateIssued = findFirstElement(originInfo as XElement, byName("mods:dateIssued"));
    let placeTerm = findFirstElement(originInfo as XElement, and(byName("mods:placeTerm"), byAttr("type", "text")));

    return {
      dateIssued: dateIssued? flattenElement(dateIssued): undefined,
      placeTerm: placeTerm? flattenElement(placeTerm): undefined
    }
  })[0];
});

const url = computed(() => {
  if(!relatedItem.value) {
    return null;
  }
  return relatedItem.value.content.filter(byName("mods:location")).map((location) => {
    const urlElement = findFirstElement(location as XElement, byName("mods:url"));
    if (urlElement) {
      return getAttribute(urlElement, "xlink:href")?.value || null;
    }
    return null;
  })[0];
});

</script>


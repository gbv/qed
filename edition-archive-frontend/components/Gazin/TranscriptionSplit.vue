<template>
  <div class="row">
    <div v-if="model.originalTranscriptUrl" :class="model.translationTranscriptUrl ? `no-padding-col col-6` : `col-12`">
      <h4>{{$t('metadata.original')}}</h4>
      <GazinTranscription :tei-url="model.originalTranscriptUrl" />
    </div>
    <div v-if="model.translationTranscriptUrl" :class="model.originalTranscriptUrl ? `no-padding-col col-6` : `col-12`">
      <h4>{{$t('metadata.translation')}}</h4>
      <GazinTranscription :tei-url="model.translationTranscriptUrl" />
    </div>
    <div v-if="!model.originalTranscriptUrl && !model.translationTranscriptUrl" class="col-12">
      <!-- spinner -->
      <div class="text-center">
        <span class="spinner-border" role="status" aria-hidden="true"></span>
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import {
  byName,
  findElement, findFirstElement,
  flattenElement,
  getAttribute,
  type XElement,
  XMLApi
} from "~/api/XMLApi";
import {GazinFilterParams} from "~/api/GazinSearchHelper";


const model = reactive({
  originalTranscriptUrl: undefined as string | undefined,
  translationTranscriptUrl: undefined as string | undefined
});

const props = defineProps<{ mycoreId: string, backendUrl: string, xml: XElement }>();

const getMods = (xml: XElement) => {
  return findFirstElement(xml, byName("mods:mods"));
}

const getRelatedItems = (xml: XElement) => {
  return xml.content.filter(byName("mods:relatedItem")) as XElement[];
}

const filterRelatedItemsByType = (relatedItems: XElement[], type: string) => {
  return relatedItems.filter((relatedItem) => {
    return getAttribute(relatedItem, "type")?.value == type;
  });
}

const mods = computed(() => {
  if (props.xml == null) {
    return undefined;
  }
  return getMods(props.xml);
});

const relatedItems = computed(() => {
  if (mods.value == null) {
    return undefined;
  }
  return getRelatedItems(mods.value);
});

const relatedItemsOriginal = computed(() => {
  if (relatedItems.value == null) {
    return undefined;
  }
  return filterRelatedItemsByType(relatedItems.value, "original");
});

const getFirstDerivateInfo = (xml: XElement) => {
  const derobjectElement = findFirstElement(xml, byName("derobject"));
  if (derobjectElement != null) {
    const maindoc = flattenElement(findFirstElement(derobjectElement, byName("maindoc")));
    let derid = getAttribute(derobjectElement, "xlink:href")?.value;
    return {
      id: derid,
      mainDoc: maindoc
    };
  } else {
    return null;
  }
}


const transcriptionUrl = computed(() => {
  if (props.xml == null) {
    return undefined;
  }

  const info = getFirstDerivateInfo(props.xml);
  if (info == null || info.id == null || info.mainDoc == null) {
    return undefined;
  }
  return `${props.backendUrl}api/v2/objects/${props.mycoreId}/derivates/${info.id}/contents/${info.mainDoc}`;
});

const resolveOriginId = async () => {
  const json = await fetch(`${props.backendUrl}api/v1/search?q=mods.relatedItem.original:${props.mycoreId}&wt=json&fq=${GazinFilterParams.join("%20AND%20")}`, {
    method: "GET",
    headers: {
      "Accept": "application/json",
    }
  }).then((resp) => resp.json());

  const ids = [];
  for (const doc of json?.response?.docs) {
    ids.push(doc.id);
  }

  if (ids.length == 0) {
    return undefined;
  }

  return ids[0];
};


async function getObjectInfo(objectId: string) {
  const req = await fetch(`${props.backendUrl}api/v2/objects/${objectId}`, {
    method: "GET",
    headers: {
      "Accept": "application/xml",
    }
  });

  const text = await req.text();
  return {xml: await XMLApi(text), id: objectId};
}

const isTranslation = computed(() => {
  return relatedItemsOriginal.value != null && relatedItemsOriginal.value.length > 0;
});

const loadOtherTranscriptionUrl = async () => {
  const mods = getMods(props.xml);
  if (mods == null) {
    return undefined;
  }
  const relatedItems = getRelatedItems(mods);
  const relatedItemsOriginal = filterRelatedItemsByType(relatedItems, "original");

  if (relatedItemsOriginal != null && relatedItemsOriginal.length > 0) {
    let translationId = getAttribute(relatedItemsOriginal[0], "xlink:href")?.value;
    if (translationId == null) {
      return undefined;
    }
    const object = await getObjectInfo(translationId);
    if (object == null) {
      return undefined;
    }
    const info = getFirstDerivateInfo(object.xml);
    if (info == null || info.id == null || info.mainDoc == null) {
      return undefined;
    }
    return `${props.backendUrl}api/v2/objects/${object.id}/derivates/${info.id}/contents/${info.mainDoc}`;
  } else {
    const originId = await resolveOriginId();
    if (originId == null) {
      return undefined;
    }

    const object = await getObjectInfo(originId);
    if (object == null) {
      return undefined;
    }
    const info = getFirstDerivateInfo(object.xml);
    if (info == null || info.id == null || info.mainDoc == null) {
      return undefined;
    }
    return `${props.backendUrl}api/v2/objects/${object.id}/derivates/${info.id}/contents/${info.mainDoc}`;
  }

}

model.originalTranscriptUrl = !isTranslation.value ?  transcriptionUrl.value : await loadOtherTranscriptionUrl();
model.translationTranscriptUrl = isTranslation.value ? transcriptionUrl.value : await loadOtherTranscriptionUrl();

</script>

<style scoped>
.no-padding-col {
  padding-left: 0;
  padding-right: 0;
}

h4 {
  padding-left: 1em;
}
</style>
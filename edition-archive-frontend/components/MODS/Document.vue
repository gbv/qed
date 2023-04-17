<template>
  <div>
    <h2 v-for="title in titles" :lang="title.language">
      {{ title.title }}
      <template v-if="title.subtitle">
        : {{ title.subtitle }}
      </template>
    </h2>

    <div class="abstract" v-if="fullAbstract?.length">
      <span v-if="fullAbstract?.length < 200">
        {{ fullAbstract }}
      </span>
      <span v-else-if="!model.showFullAbstract">
        {{ shortAbstract }}
        <a href="#" @click="model.showFullAbstract = true">
          {{ $t("sosu.metadata.abstract.showMore") }}
        </a>
      </span>
      <span v-else>
        {{ fullAbstract }}
        <a href="#" @click="model.showFullAbstract = false">
          {{ $t("sosu.metadata.abstract.showLess") }}
        </a>
      </span>
    </div>

    <iframe v-if="viewerLink" :src="viewerLink" class="viewer" frameborder="0" scrolling="no"/>

    <div class="metadata mt-3">
      <h3>Metadaten</h3>

      <SovietSurvivorsMetaKeyValue v-if="genres != null && genres.length>0">
        <template #key>
          {{ $t("sosu.metadata.genre") }}
        </template>
        <template #value>
          <ol class="genreList">
            <li class="genre" v-for="genre in genres">
              <MODSClassification :classId="genre.classId" :categId="genre.categId" />
            </li>
          </ol>
        </template>
      </SovietSurvivorsMetaKeyValue>

      <SovietSurvivorsMetaKeyValue v-for="(names, role) in namesByRole">
        <template #key>
          {{ $t(`sosu.metadata.name.role.${role}`) }}
        </template>
        <template #value>
          <ol class="nameList">
            <li class="name" v-for="name in names">
              <MODSName :name="name" />
            </li>
          </ol>
        </template>
      </SovietSurvivorsMetaKeyValue>

      <SovietSurvivorsMetaKeyValue v-if="relatedItemsOriginal?.length > 0">
        <template #key>
          {{ $t("sosu.metadata.related.original") }}
        </template>
        <template #value>
          <span v-for="relatedItem in relatedItemsOriginal">
            <nuxt-link
              :to="`/soviet-survivors/documents/${getMyCoReIdNumber(getAttribute(relatedItem, 'xlink:href')?.value)}`">
              {{ getTitles(relatedItem)[0].title }}
            </nuxt-link>
          </span>
        </template>
      </SovietSurvivorsMetaKeyValue>

      <SovietSurvivorsMetaKeyValue v-if="dateIssued?.length > 0">
        <template #key>
          {{ $t("sosu.metadata.dateIssued") }}
        </template>
        <template #value>
          <span v-for="date in dateIssued">
            {{ date }}
          </span>
        </template>
      </SovietSurvivorsMetaKeyValue>

      <SovietSurvivorsMetaKeyValue v-if="typeOfResource">
        <template #key>
          {{ $t("sosu.metadata.typeOfResource") }}
        </template>
        <template #value>
          {{ typeOfResource }}
        </template>
      </SovietSurvivorsMetaKeyValue>

      <SovietSurvivorsMetaKeyValue v-if="physicalDescriptionExtent?.length > 0">
        <template #key>
          {{ $t("sosu.metadata.extent") }}
        </template>
        <template #value>
          <span v-for="extent in physicalDescriptionExtent">
            {{ extent }}
          </span>
        </template>
      </SovietSurvivorsMetaKeyValue>


      <SovietSurvivorsMetaKeyValue v-for="topicSubject in topicSubjects">
        <template #key>
          {{ $t("sosu.metadata.subject.topic") }}
        </template>
        <template #value>
          <ol class="subjectTopicList">
            <li class="subjectTopic" v-for="topic in topicSubject.topic">
              {{ topic }}
            </li>
          </ol>

        </template>
      </SovietSurvivorsMetaKeyValue>


      <template v-for="geoSubject in geographicSubjects">
        <SovietSurvivorsMetaKeyValue v-if="geoSubject.geographic.length > 1">
          <template #key>
            {{ $t("sosu.metadata.subject.geographic") }}
          </template>
          <template #value>
            <ol class="subjectGeographicList">
              <li class="subjectGeographic" v-for="geographic in geoSubject.geographic">
                {{ geographic }}
              </li>
            </ol>
          </template>
        </SovietSurvivorsMetaKeyValue>
        <SovietSurvivorsMetaKeyValue v-if="geoSubject.coordinates.length>0">
          <template #key>
            {{ $t("sosu.metadata.subject.coordinates") }}
          </template>
          <template #value>
            <ol class="subjectCoordinateList" v-if="geoSubject.coordinates.length>0">
              <li class="subjectCoordinates" v-for="subjectCoordinate in geoSubject.coordinates">
                {{ subjectCoordinate }}
                <button class="btn btn-primary btn-sm" v-on:click="toggleShowMap(subjectCoordinate)">
                  {{
                    $t(!mapVisible(subjectCoordinate) ? "sosu.metadata.subject.showMap" : "sosu.metadata.subject.hideMap")
                  }}
                </button>
              </li>
            </ol>
          </template>
        </SovietSurvivorsMetaKeyValue>
        <div class="mt-2" v-if="geoSubject.coordinates.length>0" v-for="subjectCoordinate in geoSubject.coordinates">
          <client-only>
            <SovietSurvivorsMapCoordinates v-if="mapVisible(subjectCoordinate)" :coordinates="subjectCoordinate"/>
          </client-only>
        </div>
      </template>

      <SovietSurvivorsMetaKeyValue v-if="shelfLocator">
        <template #key>
          {{ $t("sosu.metadata.shelfLocator") }}
        </template>
        <template #value>
          {{ shelfLocator}}
        </template>
      </SovietSurvivorsMetaKeyValue>
    </div>
  </div>
</template>

<script setup lang="ts">

import {
  and,
  byAttr,
  byName,
  findElement,
  findFirstElement,
  flattenElement,
  getAttribute,
  XElement, XNode
} from "@mycore-org/xml-json-api";

import {getGenre, getNames, getSubjects, getTitles, Name} from "~/api/Mods";
import {getMyCoReIdNumber} from "~/api/MyCoRe";
import Classification from "~/components/MODS/Classification.vue";
import {trimString} from "~/api/Utils";


const {$sovietSurviorsURL} = useNuxtApp();
const sovietSurviorsURL = $sovietSurviorsURL();

const model = reactive({showCoordinates: [] as string[], showFullAbstract: false as boolean});

const mapVisible = (coord: string) => {
  return model.showCoordinates.indexOf(coord) > -1;
}

const toggleShowMap = (coord: string) => {
  if (model.showCoordinates.indexOf(coord) > -1) {
    model.showCoordinates.splice(model.showCoordinates.indexOf(coord), 1)
  } else {
    model.showCoordinates.push(coord);
  }
}

const props = defineProps<{
  xml: XElement
}>()


const mods = computed(() => {
  return findFirstElement(props.xml, byName("mods:mods")) as XElement;
});

const titles = computed(() => {
  return getTitles(mods.value);
});



const fullAbstract = computed(() => {
  return flattenElement(findFirstElement(mods.value, and(byName("mods:abstract"), (el:XNode) => !byAttr("altFormat")(el)))) || undefined;
});

const excerptLength = 200;
const shortAbstract = computed(() => {
  return trimString(fullAbstract.value || null, excerptLength);
});

const names = computed(() => {
  return getNames(mods.value);
});

const namesByRole = computed(() => {
  const nbr = {} as Record<string, Name[]>;
  names.value.forEach((name) => {
    name.roles.forEach((role) => {
      if (!nbr.hasOwnProperty(role)) {
        nbr[role]= [name];
      } else {
        nbr[role].push(name);
      }
    });
  });
  return nbr;
});



const relatedItems = computed(() => {
  return mods.value.content.filter(byName("mods:relatedItem")) as XElement[];
});

const relatedItemsOriginal = computed(() => {
  return relatedItems.value.filter((relatedItem) => {
    return getAttribute(relatedItem, "type")?.value == "original";
  });
});

const originInfo = computed(() => {
  return mods.value.content.filter(byName("mods:originInfo")) as XElement[];
});

const originInfoPublication = computed(() => {
  return originInfo.value.filter(byAttr("eventType", "publication"));
});

const dateIssued = computed(() => {
  return originInfoPublication.value.map((oiPublication) => {
    return flattenElement(findFirstElement(oiPublication, and(byName("mods:dateIssued"), byAttr("encoding", "w3cdtf"))));
  });
});

const typeOfResource = computed(() => {
  return flattenElement(findFirstElement(mods.value, byName("mods:typeOfResource")));
});

const physicalDescription = computed(() => {
  return findFirstElement(mods.value, byName("mods:physicalDescription")) as XElement;
});

const physicalDescriptionExtent = computed(() => {
  if(physicalDescription.value == null) {
    return [];
  }
  return (findElement(physicalDescription.value, byName("mods:extent")) as XElement[])
    .map((extend) => flattenElement(extend)) as string[];
});

const topicSubjects = computed(() => {
  let subjects = getSubjects(mods.value);
  return subjects.filter(subject => subject.topic.length > 0);
});

const geographicSubjects = computed(() => {
  let subjects = getSubjects(mods.value);
  return subjects.filter(subject => subject.geographic.length > 0 || subject.coordinates.length > 0);
});

const shelfLocator = computed(() => {
  return flattenElement(findFirstElement(mods.value, byName("mods:shelfLocator")));
});

const genres = computed(() => {
  return getGenre(mods.value);
})



const viewerLink = computed(() => {
  let firstDerivate = findFirstElement(props.xml, byName("derobject"));

  if (firstDerivate == null) {
    return undefined;
  }
  let href = getAttribute(firstDerivate, "xlink:href");
  if (href == null) {
    return undefined;
  }

  let maindocElem = findFirstElement(firstDerivate, byName("maindoc"));
  if (maindocElem == null) {
    return undefined;
  }

  let maindoc = flattenElement(maindocElem);
  if (maindoc == null) {
    return undefined;
  }

  return sovietSurviorsURL + "rsc/viewer/" + href.value + "/" + maindoc + "&embedded=true&XSL.Style=frame";

});


</script>

<style scoped>

.viewer {
  display: block;
  margin-top: 1rem;
  width: 100%;
  height: 500px;
}

/* display topic list elements as normal text */
.subjectTopicList li, .subjectGeographicList li, .subjectCoordinateList li, .nameList li, .genreList li {
  list-style-type: none;
  display: block;
}



/* remove padding and margin from list elements */
.subjectTopicList, .subjectGeographicList, .subjectCoordinateList, .nameList, .genreList {
  padding: 0;
  margin: 0;
}
</style>
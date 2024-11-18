<template>
  <div>

    <h2 v-if="mainTitle">
      {{ mainTitle.title }}
      <template v-if="mainTitle.subtitle">
        : {{ mainTitle.subtitle }}
      </template>
    </h2>

    <ul class="nav nav-tabs mt-4">
      <li class="nav-item" v-for="lang in titleAndAbstracts.keys()">
        <a :href="`#${lang}`" :class="`nav-link${currentAbstractLanguage == lang ? ' active' : ''}`"
           v-on:click.prevent="model.currentAbstractLang = lang">
          <MODSClassification class-id="rfc5646" :categ-id="lang"/>
        </a>
      </li>
    </ul>


    <h2 class="mt-4" v-if="mainTitle?.title != currentTitle" :lang="currentAbstractLanguage">{{ currentTitle }}</h2>

    <div class="abstract" :class="mainTitle?.title == currentTitle? 'mt-4' : ''" v-if="fullAbstract?.length">
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

      <SovietSurvivorsMetaKeyValue v-if="documentLanguages != null && documentLanguages.length>0">
        <template #key>
          {{ $t("sosu.metadata.language") }}
        </template>
        <template #value>
          <ol class="languageList">
            <li class="language" v-for="language in documentLanguages">
              <MODSClassification class-id="rfc5646" :categ-id="language"/>
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

      <SovietSurvivorsMetaKeyValue v-if="model.translations?.length > 0">
        <template #key>
          {{ $t("sosu.metadata.related.translation") }}
        </template>
        <template #value>
          <span v-for="translation in model.translations">
            <nuxt-link
              :to="`/soviet-survivors/documents/${getMyCoReIdNumber(translation.id)}`">
              {{ translation.title }}
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


      <SovietSurvivorsMetaKeyValue v-if="downloadLink">
        <template #key>
          {{ $t("sosu.metadata.download") }}
        </template>
        <template #value>
          <a :href="downloadLink" target="_blank">
            {{ $t("sosu.metadata.download") }}
          </a>
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
  XElement,
  XNode
} from "@mycore-org/xml-json-api";

import type {Name} from "~/api/Mods";
import {getGenre, getNames, getSubjects, getTitles} from "~/api/Mods";
import {getMyCoReIdNumber} from "~/api/MyCoRe";
import {trimString} from "~/api/Utils";
import {SoSuFilterParams} from "~/api/SearchHelper";


const {$sovietSurviorsURL, $sovietSurvivorsSolrURL} = useNuxtApp();
const sovietSurviorsURL = $sovietSurviorsURL();
const sovietSurvivorsSolrURL = $sovietSurvivorsSolrURL();


interface Translation {
  title: string;
  id: string;
}

const model = reactive({
    showCoordinates: [] as string[],
    showFullAbstract: false as boolean,
    currentAbstractLang: null as string | null,
    translations: [] as Translation[]
  }
);

const searchOriginals = async () => {
  const json = await fetch(`${sovietSurvivorsSolrURL}mir/select?q=mods.relatedItem.original:${props.id}&wt=json&fq=${SoSuFilterParams.join("%20AND%20")}`, {
    method: "GET",
    headers: {
      "Accept": "application/json",
    }
  }).then((resp) => resp.json());

  const translations = [] as Translation[];

  for (const doc of json?.response?.docs) {
    translations.push({id: doc["id"], title: doc["mods.title.main"]});
  }

  return translations;
};

model.translations = await searchOriginals();

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
  xml: XElement,
  id: string
}>()


const mods = computed(() => {
  return findFirstElement(props.xml, byName("mods:mods")) as XElement;
});

interface TitleAbstractSubtitle {
  title?: string|null;
  subtitle?: string|null;
  abstract?: string|null;
}

const mainTitle = computed(() => {
  return getTitles(mods.value).find((title) => !title.type);
});

const titleAndAbstracts = computed(() => {
  const abstracts = findElement(mods.value, and(byName("mods:abstract"), (el: XNode) => !byAttr("altFormat")(el)));
  const map = new Map<string, TitleAbstractSubtitle>();

  abstracts.forEach((abstract => {
    const lang = getAttribute(abstract, "xml:lang")?.value;
    if (lang) {
      if (map.has(lang)) {
        const obj = map.get(lang) as TitleAbstractSubtitle;
        obj.abstract = flattenElement(abstract);
      } else {
        map.set(lang, {abstract: flattenElement(abstract)});
      }
    }
  }));

  getTitles(mods.value).forEach((title) => {
    const lang = title.language;
    if (lang) {
      if (map.has(lang)) {
        const obj = map.get(lang) as TitleAbstractSubtitle;
        obj.title = title.title;
        obj.subtitle = title.subtitle;
      } else {
        map.set(lang, {
          title: title.title,
          subtitle: title.subtitle
        });
      }
    }
  });

  return map;
})

const documentLanguages = computed(() => {
  const modsLanguage = findElement(mods.value, byName("mods:language"));
  const langs = [] as string[];
  for (const lang of modsLanguage) {
    const langTerm = findElement(lang, byName("mods:languageTerm"));
    if (langTerm != null) {
      langTerm.forEach((term) => {
        const lang = flattenElement(term);
        if (lang != null) {
          langs.push(lang);
        }
      });
    }
  }
  return langs;
});

const currentAbstractLanguage = computed(() => {
  if (model.currentAbstractLang) {
    return model.currentAbstractLang;
  }

  const avail = documentLanguages.value
    .filter((lang) => titleAndAbstracts.value.has(lang));

  if (avail.length > 0) {
    return avail[0];
  }

  return titleAndAbstracts.value.keys().next().value;

});

const currentTitle = computed(() => {
  return titleAndAbstracts.value.get(currentAbstractLanguage.value)?.title;
});

const currentAbstract = computed(() => {
  return titleAndAbstracts.value.get(currentAbstractLanguage.value)?.abstract;
});

const fullAbstract = computed(() => {
  return currentAbstract.value || "";
});

const excerptLength = 200;

const shortAbstract = computed(() => {
  const abstract = currentAbstract.value;
  if (abstract) {
    return trimString(currentAbstract.value, excerptLength);
  }
  return "";
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

const downloadLink = computed(() => {
  if (findFirstElement(props.xml, byName("derobject")) != null) {
    return `${sovietSurviorsURL}servlets/SovietSurvivorsExportServlet/?id=${props.id}`;
  } else {
    return undefined;
  }
})

const genres = computed(() => {
  return getGenre(mods.value);
});


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
.subjectTopicList li, .subjectGeographicList li, .subjectCoordinateList li, .nameList li, .genreList li, .languageList li {
  list-style-type: none;
  display: block;
}



/* remove padding and margin from list elements */
.subjectTopicList, .subjectGeographicList, .subjectCoordinateList, .nameList, .genreList, .languageList {
  padding: 0;
  margin: 0;
}
</style>
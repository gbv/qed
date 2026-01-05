<template>
  <div v-if="mods">

    <h2 v-if="mainTitle">
      {{ mainTitle.title }}
      <template v-if="mainTitle.subtitle">
        : {{ mainTitle.subtitle }}
      </template>
    </h2>
    <div v-if="model.translations?.length > 0">
      {{ $t("metadata.related.translation") }}:
      <span
        class="sosu-document-translations"
        v-for="translation in model.translations">
        <nuxt-link
          :to="`${projectDocumentUrlPrefix}${getMyCoReIdNumber(translation.id)}`">
          {{ translation.title }}
        </nuxt-link>
      </span>
    </div>

    <div v-if="relatedItemsOriginal?.length > 0">
      {{ $t("metadata.related.original") }}:
      <span
        class="sosu-document-original"
        v-for="relatedItem in relatedItemsOriginal">
        <nuxt-link
          :to="`${projectDocumentUrlPrefix}${getMyCoReIdNumber(getAttribute(relatedItem, 'xlink:href')?.value)}`">
          {{ getTitles(relatedItem)[0].title }}
        </nuxt-link>
      </span>
    </div>

    <ul v-if="titleAndAbstracts.size>1" class="nav nav-tabs mt-4">
      <li class="nav-item" v-for="lang in titleAndAbstracts.keys()">
        <a :href="`#${lang}`" :class="`nav-link${currentAbstractLanguage == lang ? ' active' : ''}`"
           v-on:click.prevent="model.currentAbstractLang = lang">
          <MODSClassification :app-url="backendUrl" class-id="rfc5646" :categ-id="lang"/>
        </a>
      </li>
    </ul>
    <!--
    <div class="mt-4" v-if="titleAndAbstracts.size==1">
      <MODSClassification  :app-url="backendUrl" class-id="rfc5646" :categ-id="titleAndAbstracts.keys().toArray()[0]"/>
    </div>
    -->



    <h2 class="mt-4" v-if="mainTitle?.title != currentTitle" :lang="currentAbstractLanguage">{{ currentTitle }}</h2>

    <div class="abstract" :class="mainTitle?.title == currentTitle? 'mt-4' : ''" v-if="fullAbstract?.length">
      <span v-if="fullAbstract?.length < 200">
        {{ fullAbstract }}
      </span>
      <span v-else-if="!model.showFullAbstract">
        {{ shortAbstract }}
        <a href="#" @click="model.showFullAbstract = true">
          {{ $t("metadata.abstract.showMore") }}
        </a>
      </span>
      <span v-else>
        {{ fullAbstract }}
        <a href="#" @click="model.showFullAbstract = false">
          {{ $t("metadata.abstract.showLess") }}
        </a>
      </span>
    </div>

    <slot name="media" />

    <div class="metadata mt-3">
      <h3>Metadaten</h3>

      <MODSMetaKeyValue v-if="!props.hideGenre && genres != null && genres.length>0">
        <template #key>
          {{ $t("metadata.genre") }}
        </template>
        <template #value>
          <ol class="genreList">
            <li class="genre" v-for="genre in genres">
              <MODSClassification :app-url="backendUrl"  :classId="genre.classId" :categId="genre.categId" />
            </li>
          </ol>
        </template>
      </MODSMetaKeyValue>

      <MODSMetaKeyValue v-if="documentLanguages != null && documentLanguages.length>0">
        <template #key>
          {{ $t("metadata.language") }}
        </template>
        <template #value>
          <ol class="languageList">
            <li class="language" v-for="language in documentLanguages">
              <MODSClassification :app-url="backendUrl" class-id="rfc5646" :categ-id="language"/>
            </li>
          </ol>
        </template>
      </MODSMetaKeyValue>

      <MODSMetaKeyValue v-for="classification in classifications">
        <template #key>
          <MODSClassification :app-url="backendUrl" :classId="classification.classId" />
        </template>
        <template #value>
          <MODSClassification :app-url="backendUrl" :classId="classification.classId" :categId="classification.categId" />
        </template>
      </MODSMetaKeyValue>


      <MODSMetaKeyValue v-for="(names, role) in namesByRole">
        <template #key>
          <MODSClassification :app-url="backendUrl" class-id="marcrelator" :categ-id="role" />
        </template>
        <template #value>
          <ol class="nameList">
            <li class="name" v-for="name in names">
              <MODSName :app-url="props.backendUrl" :name="name" />
            </li>
          </ol>
        </template>
      </MODSMetaKeyValue>

      <!--
      <MODSMetaKeyValue v-if="relatedItemsOriginal?.length > 0">
        <template #key>
          {{ $t("metadata.related.original") }}
        </template>
        <template #value>
          <span v-for="relatedItem in relatedItemsOriginal">
            <nuxt-link
              :to="`/soviet-survivors/documents/${getMyCoReIdNumber(getAttribute(relatedItem, 'xlink:href')?.value)}`">
              {{ getTitles(relatedItem)[0].title }}
            </nuxt-link>
          </span>
        </template>
      </MODSMetaKeyValue>

      <MODSMetaKeyValue v-if="model.translations?.length > 0">
        <template #key>
          {{ $t("metadata.related.translation") }}
        </template>
        <template #value>
          <span v-for="translation in model.translations" class="sosu-document-translations">
            <nuxt-link
              :to="`/soviet-survivors/documents/${getMyCoReIdNumber(translation.id)}`">
              {{ translation.title }}
            </nuxt-link>
          </span>
        </template>
      </MODSMetaKeyValue>
      -->

      <MODSMetaKeyValue v-if="dateIssued?.length > 0">
        <template #key>
          {{ $t("metadata.dateIssued") }}
        </template>
        <template #value>
          <span v-for="date in dateIssued">
            {{ date }}
          </span>
        </template>
      </MODSMetaKeyValue>

      <MODSMetaKeyValue v-if="physicalDescriptionExtent?.length > 0">
        <template #key>
          {{ $t("metadata.extent") }}
        </template>
        <template #value>
          <span v-for="extent in physicalDescriptionExtent">
            {{ extent }}
          </span>
        </template>
      </MODSMetaKeyValue>


      <MODSMetaKeyValue v-for="topicSubject in topicSubjects">
        <template #key>
          {{ $t("metadata.subject.topic") }}
        </template>
        <template #value>
          <ol class="subjectTopicList">
            <li class="subjectTopic" v-for="topic in topicSubject.topic">
              <nuxt-link
                :to="`/soviet-survivors/search?q=%22${topic}%22`">
                {{ topic }}
              </nuxt-link>
            </li>
          </ol>

        </template>
      </MODSMetaKeyValue>


      <template v-for="geoSubject in geographicSubjects">
        <MODSMetaKeyValue v-if="geoSubject.geographic.length > 1">
          <template #key>
            {{ $t("metadata.subject.geographic") }}
          </template>
          <template #value>
            <ol class="subjectGeographicList">
              <li class="subjectGeographic" v-for="geographic in geoSubject.geographic">
                {{ geographic }}
              </li>
            </ol>
          </template>
        </MODSMetaKeyValue>

        <MODSMetaKeyValue v-if="creationDate">
          <template #key>
            {{ $t("metadata.creationDate") }}
          </template>
          <template #value>
            {{ creationDate }}
          </template>
        </MODSMetaKeyValue>

        <MODSMetaKeyValue v-if="creationPlace">
          <template #key>
            {{ $t("metadata.creationPlace") }}
          </template>
          <template #value>
            {{ creationPlace }}
          </template>
        </MODSMetaKeyValue>

        <MODSMetaKeyValue v-if="geoSubject.coordinates.length>0">
          <template #key>
            {{ $t("metadata.subject.coordinates") }}
          </template>
          <template #value>
            <ol class="subjectCoordinateList" v-if="geoSubject.coordinates.length>0">
              <li class="subjectCoordinates" v-for="subjectCoordinate in geoSubject.coordinates">
                {{ subjectCoordinate }}
                <button class="btn btn-primary btn-sm" v-on:click="toggleShowMap(subjectCoordinate)">
                  {{
                    $t(!mapVisible(subjectCoordinate) ? "metadata.subject.showMap" : "metadata.subject.hideMap")
                  }}
                </button>
              </li>
            </ol>
          </template>
        </MODSMetaKeyValue>
        <div class="mt-2" v-if="geoSubject.coordinates.length>0" v-for="subjectCoordinate in geoSubject.coordinates">
          <client-only>
            <MapCoordinates v-if="mapVisible(subjectCoordinate)" :coordinates="subjectCoordinate"/>
          </client-only>
        </div>
      </template>

      <MODSMetaKeyValue v-if="archive">
        <template #key>
          {{ $t("metadata.archive") }}
        </template>
        <template #value>
          <MODSClassification :app-url="backendUrl" :classId="archive.classId" :categId="archive.categId" />
        </template>
      </MODSMetaKeyValue>

      <MODSMetaKeyValue v-if="shelfLocator">
        <template #key>
          {{ $t("metadata.shelfLocator") }}
        </template>
        <template #value>
          {{ shelfLocator}}
        </template>
      </MODSMetaKeyValue>

      <slot name="downloadLink" v-if="slots.downloadLink" />

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
  getAttribute, not,
  type XElement,
  type XNode
} from "~/api/XMLApi";

import type {Name} from "~/api/Mods";
import {getGenre, getNames, getSubjects, getTitles} from "~/api/Mods";
import {getMyCoReIdNumber} from "~/api/MyCoRe";
import {trimString} from "~/api/Utils";

const slots = defineSlots<{
  media?: {};
  downloadLink?: {};
}>()


interface Translation {
  title: string;
  id: string;
}

const model = reactive({
    hideCoordinates: [] as string[],
    showFullAbstract: false as boolean,
    currentAbstractLang: null as string | null,
    translations: [] as Translation[]
  }
);

const props = defineProps<{
  xml: XElement,
  id: string,
  projectDocumentUrlPrefix: string,
  backendUrl: string,
  filterParams: string[],
  showClassifications?: string[],
  hideGenre?: boolean
}>()

const searchOriginals = async () => {
  const json = await fetch(`${props.backendUrl}api/v1/search?q=mods.relatedItem.original:${props.id}&wt=json&fq=${props.filterParams.join("%20AND%20")}`, {
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
  return model.hideCoordinates.indexOf(coord) == -1;
}

const toggleShowMap = (coord: string) => {
  if (model.hideCoordinates.indexOf(coord) > -1) {
    model.hideCoordinates.splice(model.hideCoordinates.indexOf(coord), 1)
  } else {
    model.hideCoordinates.push(coord);
  }
}




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
  const abstracts = mods.value.content.filter(el=>{
    if(el.type != "Element") {
      return false;
    }
    if(el.name != "mods:abstract") {
      return false;
    }
    if(getAttribute(el, "altFormat")) {
      return false;
    }
    return true;
  }) as XElement[];
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
  const langs = [] as string[];
  mods.value.content.forEach((lang) => {
    if(lang.type != "Element") {
      return;
    }
    if(lang.name != "mods:language") {
      return;
    }
    const langTerm = findElement(lang, byName("mods:languageTerm"));
    if (langTerm != null) {
      langTerm.forEach((term) => {
        const lang = flattenElement(term);
        if (lang != null) {
          langs.push(lang);
        }
      });
    }
  });
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
  let value = currentAbstractLanguage.value;
  if(!value) {
    return ""
  }
  return titleAndAbstracts.value.get(value)?.title;
});

const currentAbstract = computed(() => {
  let key = currentAbstractLanguage.value;
  if(!key) {
    return "";
  }
  return titleAndAbstracts.value.get(key)?.abstract;
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

const archive = computed( ()=> {
  const el = findFirstElement(mods.value, and(byName('mods:classification'), byAttr('authorityURI', 'https://qed.perspectivia.net/soviet-survivors-backend/classifications/sursurv_archives')));
  if(el == null) {
    return undefined;
  }
  const valueURI = getAttribute(el, 'valueURI')?.value;
  if(!valueURI) {
    return undefined;
  }
  const categValue = valueURI.substring(valueURI.lastIndexOf("#")+1);

  return {classId: 'sursurv_archives', categId: categValue};
});

const classifications = computed(()=> {
  const el = mods.value.content.filter(and(byName('mods:classification'), byAttr('authorityURI'), not(byAttr('generator')))) as XElement[];
  return el.map((el) => {
    const authorityURI = getAttribute(el, 'authorityURI')?.value;
    const valueURI = getAttribute(el, 'valueURI')?.value;
    return { authorityURI, valueURI };
  })
  .filter(({authorityURI, valueURI}) => authorityURI != null && valueURI != null)
  .map(({authorityURI, valueURI}) => {
    authorityURI = authorityURI as string;
    valueURI = valueURI as string;
    const classId = authorityURI.substring(authorityURI.lastIndexOf("/")+1);
    const categId = valueURI.substring(valueURI.lastIndexOf("#")+1);
    return {classId, categId};
  })
  .filter(({classId}) => {
    if (props.showClassifications && props.showClassifications.length > 0) {
      return props.showClassifications.indexOf(classId) > -1;
    }
    return false;
  })
  .filter((c) => c != null) as {classId: string, categId: string}[];
});


const shelfLocator = computed(() => {
  return flattenElement(findFirstElement(mods.value, byName("mods:shelfLocator")));
});


const genres = computed(() => {
  return getGenre(mods.value);
});

const creationPlace = computed(() => {
  const originInfo = findFirstElement(mods.value, and(byName("mods:originInfo"), byAttr("eventType", "creation")));
  if (originInfo == null) {
    return null;
  }
  return flattenElement(findFirstElement(originInfo, byName("mods:placeTerm")));
});

const creationDate = computed(() => {
  const originInfo = findFirstElement(mods.value, and(byName("mods:originInfo"), byAttr("eventType", "creation")));
  if (originInfo == null) {
    return null;
  }
  return flattenElement(findFirstElement(originInfo, byName("mods:dateCreated")));
});




</script>

<style scoped>



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
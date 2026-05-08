<template>
  <div v-if="mods">

    <h2 v-if="displayTitle">
      {{ displayTitle.title }}
      <template v-if="displayTitle.subtitle">
        : {{ displayTitle.subtitle }}
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

    <template v-if="abstracts.size > 0">
      <ul v-if="abstracts.size > 1" class="nav nav-tabs mt-4">
        <li class="nav-item" v-for="lang in abstracts.keys()">
          <a :href="`#${lang}`" :class="`nav-link${currentAbstractLanguage == lang ? ' active' : ''}`"
             v-on:click.prevent="model.currentAbstractLang = lang">
            <MODSClassification :app-url="backendUrl" class-id="rfc5646" :categ-id="lang"/>
          </a>
        </li>
      </ul>

      <div class="abstract mt-4" v-if="fullAbstract?.length">
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
    </template>

    <slot name="media" />

    <div class="metadata mt-3">
      <h3>{{ $t('metadata.heading') }}</h3>

      <template v-for="section in effectiveMetadataOrder" :key="section">

        <!-- titles -->
        <template v-if="section === 'titles'">
          <MODSMetaKeyValue v-for="title in allTitles" :key="title.title">
            <template #key>
              {{ $te(`metadata.titleType.${title.type || 'main'}`) ? $t(`metadata.titleType.${title.type || 'main'}`) : title.type }}
              <span v-if="title.language" class="title-language">
                (<MODSClassification :app-url="backendUrl" class-id="rfc5646" :categ-id="title.language"/>)
              </span>
            </template>
            <template #value>
              {{ title.title }}<template v-if="title.subtitle">: {{ title.subtitle }}</template>
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- genres -->
        <template v-if="section === 'genres'">
          <MODSMetaKeyValue v-if="!props.hideGenre && genres != null && genres.length>0">
            <template #key>
              {{ $t("metadata.genre") }}
            </template>
            <template #value>
              <ol class="genreList">
                <li class="genre" v-for="genre in genres">
                  <MODSClassification :app-url="backendUrl" :classId="genre.classId" :categId="genre.categId" />
                </li>
              </ol>
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- languages -->
        <template v-if="section === 'languages'">
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
        </template>

        <!-- classifications (grouped by classId) -->
        <template v-if="section === 'classifications'">
          <MODSMetaKeyValue v-for="(categIds, classId) in classificationsByClassId" :key="classId">
            <template #key>
              <template v-if="$te(`metadata.classificationLabel.${classId}`)">
                {{ $t(`metadata.classificationLabel.${classId}`, categIds.length) }}
              </template>
              <MODSClassification v-else :app-url="backendUrl" :classId="classId" />
            </template>
            <template #value>
              <ol v-if="categIds.length > 1" class="classificationList">
                <li v-for="categId in categIds" :key="categId">
                  <MODSClassification :app-url="backendUrl" :classId="classId" :categId="categId" />
                </li>
              </ol>
              <MODSClassification v-else :app-url="backendUrl" :classId="classId" :categId="categIds[0]" />
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- notes -->
        <template v-if="section === 'notes'">
          <MODSMetaKeyValue v-for="note in sortedNotes">
            <template #key>
              <MODSClassification :app-url="backendUrl" :classId="'noteTypes'" :categ-id="note.type" />
            </template>
            <template #value>
              {{ note.content }}
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- names (top-level mods:name, excluding his) -->
        <template v-if="section === 'names'">
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
        </template>

        <!-- originInfos -->
        <template v-if="section === 'originInfos'">
          <template v-for="oi in sortedOriginInfos">
            <MODSMetaKeyValue v-for="(agents, role) in oi.agentsByRole">
              <template #key>
                <MODSClassification :app-url="backendUrl" class-id="marcrelator" :categ-id="role" />
              </template>
              <template #value>
                <ol class="nameList">
                  <li class="name" v-for="agent in agents">
                    <MODSName :app-url="props.backendUrl" :name="agent" />
                  </li>
                </ol>
              </template>
            </MODSMetaKeyValue>
            <MODSMetaKeyValue v-if="oi.displayDate">
              <template #key>{{ $te(`metadata.originInfo.${oi.eventType}.date`) ? $t(`metadata.originInfo.${oi.eventType}.date`) : $t("metadata.originInfo.date") }}</template>
              <template #value>{{ oi.displayDate }}</template>
            </MODSMetaKeyValue>
            <MODSMetaKeyValue v-if="oi.place?.placeTerm || oi.place?.placeIdentifier">
              <template #key>{{ $te(`metadata.originInfo.${oi.eventType}.place`) ? $t(`metadata.originInfo.${oi.eventType}.place`) : $t("metadata.originInfo.place") }}</template>
              <template #value>
                <MODSPlaceRef :place-term="oi.place?.placeTerm" :place-identifier="oi.place?.placeIdentifier" />
              </template>
            </MODSMetaKeyValue>
          </template>
        </template>

        <!-- dateIssued -->
        <template v-if="section === 'dateIssued'">
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
        </template>

        <!-- extent -->
        <template v-if="section === 'extent'">
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
        </template>

        <!-- topicSubjects -->
        <template v-if="section === 'topicSubjects'">
          <MODSMetaKeyValue v-for="topicSubject in topicSubjects">
            <template #key>
              {{ $t("metadata.subject.topic") }}
            </template>
            <template #value>
              <ol class="subjectTopicList">
                <li class="subjectTopic" v-for="topic in topicSubject.topic">
                  <nuxt-link
                    v-if="props.topicSearchUrlPrefix"
                    :to="`${props.topicSearchUrlPrefix}%22${topic}%22`">
                    {{ topic }}
                  </nuxt-link>
                  <span v-else>{{ topic }}</span>
                </li>
              </ol>
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- geographicSubjects -->
        <template v-if="section === 'geographicSubjects'">
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
        </template>

        <!-- citedPersons -->
        <template v-if="section === 'citedPersons'">
          <MODSMetaKeyValue v-for="(persons, role) in citedPersonsByRole" :key="role">
            <template #key>
              <MODSClassification :app-url="backendUrl" class-id="marcrelator" :categ-id="role" />
            </template>
            <template #value>
              <ol class="nameList">
                <li class="name" v-for="person in persons">
                  <MODSName :app-url="props.backendUrl" :name="person" />
                </li>
              </ol>
            </template>
          </MODSMetaKeyValue>
          <MODSMetaKeyValue v-if="citedPersonsWithoutRole.length > 0">
            <template #key>
              {{ $t("metadata.subject.citedPersons") }}
            </template>
            <template #value>
              <ol class="nameList">
                <li class="name" v-for="person in citedPersonsWithoutRole">
                  <MODSName :app-url="props.backendUrl" :name="person" />
                </li>
              </ol>
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- citedInstitutions -->
        <template v-if="section === 'citedInstitutions'">
          <MODSMetaKeyValue v-for="(institutions, role) in citedInstitutionsByRole" :key="role">
            <template #key>
              <MODSClassification :app-url="backendUrl" class-id="marcrelator" :categ-id="role" />
            </template>
            <template #value>
              <ol class="nameList">
                <li class="name" v-for="institution in institutions">
                  <MODSName :app-url="props.backendUrl" :name="institution" />
                </li>
              </ol>
            </template>
          </MODSMetaKeyValue>
          <MODSMetaKeyValue v-if="citedInstitutionsWithoutRole.length > 0">
            <template #key>
              {{ $t("metadata.subject.citedInstitutions") }}
            </template>
            <template #value>
              <ol class="nameList">
                <li class="name" v-for="institution in citedInstitutionsWithoutRole">
                  <MODSName :app-url="props.backendUrl" :name="institution" />
                </li>
              </ol>
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- archive -->
        <template v-if="section === 'archive'">
          <MODSMetaKeyValue v-if="archive">
            <template #key>
              {{ $t("metadata.archive") }}
            </template>
            <template #value>
              <MODSClassification :app-url="backendUrl" :classId="archive.classId" :categId="archive.categId" />
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- shelfLocator -->
        <template v-if="section === 'shelfLocator'">
          <MODSMetaKeyValue v-if="shelfLocator">
            <template #key>
              {{ $t("metadata.shelfLocator") }}
            </template>
            <template #value>
              {{ shelfLocator}}
            </template>
          </MODSMetaKeyValue>
        </template>

        <!-- downloadLink -->
        <template v-if="section === 'downloadLink'">
          <slot name="downloadLink" v-if="slots.downloadLink" />
        </template>

        <!-- doi -->
        <template v-if="section === 'doi'">
          <MODSMetaKeyValue v-if="doi">
            <template #key>
              {{ $t("metadata.doi") }}
            </template>
            <template #value>
              <a :href="`https://dx.doi.org/${doi}`">{{ doi }}</a>
            </template>
          </MODSMetaKeyValue>
        </template>

      </template>
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
import {getGenre, getNames, getOriginInfos, getSubjects, getTitles} from "~/api/Mods";
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

type MetadataSection = 'titles' | 'genres' | 'languages' | 'classifications' | 'notes' | 'names' | 'originInfos' | 'dateIssued' | 'extent' | 'topicSubjects' | 'geographicSubjects' | 'citedPersons' | 'citedInstitutions' | 'archive' | 'shelfLocator' | 'downloadLink' | 'doi';

const DEFAULT_METADATA_ORDER: MetadataSection[] = [
  'titles', 'names', 'dateIssued', 'originInfos', 'languages', 'genres', 'classifications',
  'topicSubjects', 'geographicSubjects', 'citedPersons', 'citedInstitutions', 'notes', 'extent',
  'archive', 'shelfLocator', 'downloadLink', 'doi'
];

const props = defineProps<{
  xml: XElement,
  id: string,
  projectDocumentUrlPrefix: string,
  backendUrl: string,
  filterParams: string[],
  showClassifications?: string[],
  hideNoteTypes?: string[],
  hideGenre?: boolean,
  preferredTitleLanguage?: string,
  originInfoOrder?: string[],
  noteTypeOrder?: string[],
  classificationOrder?: string[],
  archiveClassId?: string,
  metadataOrder?: MetadataSection[],
  topicSearchUrlPrefix?: string
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

/**
 * The title to display at the top of the document, selected by priority:
 * 1. Title matching the optional `language` prop
 * 2. Main title (no type attribute)
 * 3. First title in the MODS document
 *
 * Uses getTitles() which only searches direct children of mods:mods,
 * so titles inside mods:relatedItem are never included.
 */
const displayTitle = computed(() => {
  const titles = getTitles(mods.value);

  if (props.preferredTitleLanguage) {
    const langTitle = titles.find(t => t.language === props.preferredTitleLanguage);
    if (langTitle) {
      return langTitle;
    }
  }

  const mainTitle = titles.find(t => !t.type);
  if (mainTitle) {
    return mainTitle;
  }

  return titles[0] ?? null;
});

/**
 * All titles from the MODS document for display in the metadata section.
 * Uses getTitles() which only searches direct children of mods:mods.
 */
const allTitles = computed(() => {
  return getTitles(mods.value);
});

/**
 * Map of language code → abstract text, built from direct mods:abstract
 * children only (excludes abstracts inside mods:relatedItem).
 */
const abstracts = computed(() => {
  const abstractElements = mods.value.content.filter(el => {
    if (el.type != "Element") {
      return false;
    }
    if (el.name != "mods:abstract") {
      return false;
    }
    if (getAttribute(el, "altFormat")) {
      return false;
    }
    return true;
  }) as XElement[];

  const map = new Map<string, string>();
  abstractElements.forEach(abstract => {
    const lang = getAttribute(abstract, "xml:lang")?.value;
    if (lang) {
      map.set(lang, flattenElement(abstract) || "");
    }
  });
  return map;
});

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
  if (model.currentAbstractLang && abstracts.value.has(model.currentAbstractLang)) {
    return model.currentAbstractLang;
  }

  const avail = documentLanguages.value.filter(lang => abstracts.value.has(lang));
  if (avail.length > 0) return avail[0];

  return abstracts.value.keys().next().value ?? null;
});

const currentAbstract = computed(() => {
  const key = currentAbstractLanguage.value;
  if (!key) {
    return "";
  }
  return abstracts.value.get(key) ?? "";
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
      if (role === 'his') return;
      if (!nbr.hasOwnProperty(role)) {
        nbr[role]= [name];
      } else {
        nbr[role].push(name);
      }
    });
  });
  return nbr;
});

const originInfos = computed(() => {
  return getOriginInfos(mods.value);
});

const sortedOriginInfos = computed(() => {
  if (!props.originInfoOrder) return originInfos.value;
  const order = props.originInfoOrder;
  return [...originInfos.value].sort((a, b) => {
    const idxA = a.eventType ? order.indexOf(a.eventType) : -1;
    const idxB = b.eventType ? order.indexOf(b.eventType) : -1;
    return (idxA === -1 ? order.length : idxA) - (idxB === -1 ? order.length : idxB);
  });
});

const sortedNotes = computed(() => {
  if (!props.noteTypeOrder) return notesWithType.value;
  const order = props.noteTypeOrder;
  return [...notesWithType.value].sort((a, b) => {
    const idxA = order.indexOf(a.type);
    const idxB = order.indexOf(b.type);
    return (idxA === -1 ? order.length : idxA) - (idxB === -1 ? order.length : idxB);
  });
});

const effectiveMetadataOrder = computed(() => {
  return props.metadataOrder ?? DEFAULT_METADATA_ORDER;
});

const citedPersons = computed(() => {
  return getSubjects(mods.value)
    .flatMap(s => s.names)
    .filter(n => n.type === 'personal');
});

const citedInstitutions = computed(() => {
  return getSubjects(mods.value)
    .flatMap(s => s.names)
    .filter(n => n.type === 'corporate');
});

function groupNamesByRole(names: Name[]): Record<string, Name[]> {
  const nbr = {} as Record<string, Name[]>;
  names.forEach((name) => {
    name.roles.forEach((role) => {
      if (!nbr.hasOwnProperty(role)) {
        nbr[role] = [name];
      } else {
        nbr[role].push(name);
      }
    });
  });
  return nbr;
}

const citedPersonsByRole = computed(() => {
  return groupNamesByRole(citedPersons.value.filter(n => n.roles.length > 0));
});

const citedPersonsWithoutRole = computed(() => {
  return citedPersons.value.filter(n => n.roles.length === 0);
});

const citedInstitutionsByRole = computed(() => {
  return groupNamesByRole(citedInstitutions.value.filter(n => n.roles.length > 0));
});

const citedInstitutionsWithoutRole = computed(() => {
  return citedInstitutions.value.filter(n => n.roles.length === 0);
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

const doi = computed(() => {
  const identifierElements = mods.value.content.filter(byName("mods:identifier")) as XElement[];
  for (const identifierElement of identifierElements) {
    const typeAttr = getAttribute(identifierElement, "type");
    if (typeAttr != null && typeAttr.value === "doi") {
      return flattenElement(identifierElement);
    }
  }
  return null;
});

const archive = computed(() => {
  const classId = props.archiveClassId;
  if (!classId) return undefined;
  const el = mods.value.content
    .filter(and(byName('mods:classification'), byAttr('authorityURI')))
    .find(el => {
      const uri = getAttribute(el as XElement, 'authorityURI')?.value;
      return uri?.endsWith(`/${classId}`);
    }) as XElement | undefined;
  if (!el) return undefined;
  const valueURI = getAttribute(el, 'valueURI')?.value;
  if (!valueURI) return undefined;
  const categId = valueURI.substring(valueURI.lastIndexOf("#") + 1);
  return {classId, categId};
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

const classificationsByClassId = computed(() => {
  const grouped = {} as Record<string, string[]>;
  for (const c of classifications.value) {
    if (!grouped[c.classId]) grouped[c.classId] = [];
    grouped[c.classId].push(c.categId);
  }
  if (!props.classificationOrder) return grouped;
  const order = props.classificationOrder;
  const sorted = {} as Record<string, string[]>;
  const keys = Object.keys(grouped).sort((a, b) => {
    const idxA = order.indexOf(a);
    const idxB = order.indexOf(b);
    return (idxA === -1 ? order.length : idxA) - (idxB === -1 ? order.length : idxB);
  });
  for (const key of keys) {
    sorted[key] = grouped[key];
  }
  return sorted;
});


const notesWithType = computed(() => {
  const noteElements = mods.value.content.filter(byName("mods:note")) as XElement[];
  const notes = [] as {type: string, content: string}[];
  noteElements.forEach((noteElement) => {
    const typeAttr = getAttribute(noteElement, "type");
    if (typeAttr != null && (!props.hideNoteTypes || !props.hideNoteTypes.includes(typeAttr.value))) {
      const content = flattenElement(noteElement);
      if (content) {
        notes.push({type: typeAttr.value, content});
      }
    }
  });
  return notes;
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
.subjectTopicList li, .subjectGeographicList li, .subjectCoordinateList li, .nameList li, .genreList li, .languageList li, .classificationList li {
  list-style-type: none;
  display: block;
}



/* remove padding and margin from list elements */
.subjectTopicList, .subjectGeographicList, .subjectCoordinateList, .nameList, .genreList, .languageList, .classificationList {
  padding: 0;
  margin: 0;
}

.title-language {
  color: #999;
  font-size: 0.9em;
}
</style>

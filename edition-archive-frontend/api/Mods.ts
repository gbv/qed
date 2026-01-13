import {
  and, byAttr,
  byName,
  findElement,
  findFirstElement,
  flattenElement,
  getAttribute,
  type XElement
} from "~/api/XMLApi";

export interface Title {
  language?: string;
  type?: string;
  title: string;
  subtitle?: string;
}

export interface Name {
  displayForm?: string;
  type?: "personal" | "corporate" | "conference";
  roles: string[];
  nameParts?: NamePart[];
  affiliation?: string;
  gender?: string;
  classification?: string;
  category?: string;
  nameIdentifiers?: NameIdentifier[];
}

export interface NamePart {
  type?: string,
  value: string
}

export interface Subject {
  topic: string[];
  geographic: string[];
  coordinates: string[];
}

export interface Classification {
  classId: string;
  categId: string;
}

export interface NameIdentifier {
  type?: string;
  typeURI?: string;
  value: string;
}

export function getTitles(modsOrRelatedItem: XElement): Title[] {
  const titles: Title[] = [];
  if(modsOrRelatedItem == null) return titles;
  const titleInfo = modsOrRelatedItem.content.filter(byName("mods:titleInfo")) as XElement[];

  for (const titleInfoElement of titleInfo) {
    const title = flattenElement(findFirstElement(titleInfoElement, byName("mods:title")));
    const subtitle = flattenElement(findFirstElement(titleInfoElement, byName("mods:subTitle"))) || undefined;
    const language = getAttribute(titleInfoElement, "xml:lang")?.value;
    const type = getAttribute(titleInfoElement, "type")?.value;

    if (title != null) {
      titles.push({language, title, subtitle, type});
    }
  }

  return titles;
}


export function getNames(modsOrRelatedItem: XElement): Name[] {
  if(modsOrRelatedItem == null) return [];
  const names = modsOrRelatedItem.content.filter(byName("mods:name")) as XElement[];
  const namesResult: Name[] = [];

  for (const name of names) {
    const role = findFirstElement(name, byName("mods:role"));
    if (role == null) continue;

    const roles = findElement(role, byName("mods:roleTerm")).map(roleTerm => {
      if (getAttribute(roleTerm, "type")?.value != "code") {
        return null;
      }
      return flattenElement(roleTerm) || undefined;
    }).filter(el => el != null) as string[];

    const type = (getAttribute(name, "type")?.value as "personal" | "corporate" | "conference") || undefined;


    const nameParts = findElement(name, byName("mods:namePart")).map(namePart => {
      const type = getAttribute(namePart, "type")?.value || undefined;
      return {type, value: flattenElement(namePart) || undefined};
    }).filter(el => el.value != null) as NamePart[];

    const nameIdentifiers = findElement(name, byName("mods:nameIdentifier")).map(nameIdentifier => {
      const type = getAttribute(nameIdentifier, "type")?.value || undefined;
      const typeURI = getAttribute(nameIdentifier, "typeURI")?.value || undefined;
      const value = flattenElement(nameIdentifier) || undefined;
      return {type, typeURI, value};
    }).filter(el => el.value != null) as NameIdentifier[];

    const displayForm = flattenElement(findFirstElement(name, byName("mods:displayForm"))) || undefined;

    const affiliation = flattenElement(findFirstElement(name, byName("mods:affiliation"))) || undefined;

    const authorityURI = getAttribute(name, "authorityURI")?.value || undefined;
    const classification = authorityURI?.split("/").pop() || undefined;
    const valueURI = getAttribute(name, "valueURI")?.value || undefined;
    const category = valueURI?.split("/").pop()?.split("#").pop() || undefined;

    namesResult.push({type, roles, nameParts, displayForm, nameIdentifiers, affiliation, classification, category});


  }
  return namesResult;
}

export function getSubjects(modsOrRelatedItem: XElement): Subject[] {
  if(modsOrRelatedItem == null) return [];
  let subjects = modsOrRelatedItem.content.filter(byName("mods:subject")) as XElement[];

  return subjects.map((subject) => {
    const geographic = subject.content.filter(byName("mods:geographic"))
      .map((geographicElement) => {
        geographicElement = geographicElement as XElement;
        return flattenElement(geographicElement) as string;
      });

    const topic = subject.content.filter(byName("mods:topic"))
      .map((topicElement) => {
        topicElement = topicElement as XElement;
        return flattenElement(topicElement) as string;
      });


    const coordinates = findElement(subject, byName("mods:coordinates"))
      .map((coordinatesElement) => {
        coordinatesElement = coordinatesElement as XElement;
        return flattenElement(coordinatesElement) as string;
      });
    return {geographic, topic, coordinates};
  });
}

export function getGenre(modsOrRelatedItem: XElement): Classification[] {
  if(modsOrRelatedItem == null) return [];
  const genres = modsOrRelatedItem.content
    .filter(and(byName("mods:genre"), byAttr("type", "intern"))) as XElement[];

  return genres.map((genre) => {
    const authorityUri = getAttribute(genre, "authorityURI")?.value;
    const valueUri = getAttribute(genre, "valueURI")?.value;

    let classId = authorityUri?.split("/").pop();
    let categId = valueUri?.split("/").pop()?.split("#").pop();
    return {classId, categId}
  }).filter(clazz => clazz.classId != null && clazz.categId != null) as Classification[];
}
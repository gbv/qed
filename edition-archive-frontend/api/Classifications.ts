import {byAttr, byName, findFirstElement, getAttribute, type XElement} from "~/api/XMLApi";

export function findClassificationCategoryPath(classification: XElement, categId: string): XElement[] | null {
  const search = (parent: XElement, ancestors: XElement[]): XElement[] | null => {
    for (const content of parent.content) {
      if (content.type !== "Element") {
        continue;
      }

      const element = content as XElement;
      const categoryAncestors = element.name === "category"
        ? [...ancestors, element]
        : ancestors;

      if (element.name === "category" && getAttribute(element, "ID")?.value === categId) {
        return categoryAncestors;
      }

      const path = search(element, categoryAncestors);
      if (path) {
        return path;
      }
    }

    return null;
  };

  return search(classification, []);
}

export function getClassificationLabel(category: XElement, language: string): string | undefined {
  const localizedLabel = findFirstElement(category, (element) => {
    return byName("label")(element) && byAttr("xml:lang", language)(element);
  });
  const label = localizedLabel ?? findFirstElement(category, byName("label"));
  return label ? getAttribute(label, "text")?.value : undefined;
}
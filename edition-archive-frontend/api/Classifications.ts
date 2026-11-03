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
  const labels = category.content.filter((element): element is XElement =>
    element.type === "Element" && byName("label")(element)
  );
  const label = labels.find(byAttr("xml:lang", language)) ?? labels[0];
  return label ? getAttribute(label, "text")?.value : undefined;
}
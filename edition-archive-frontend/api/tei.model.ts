export interface TEIElement {
  type: "Element";
  name: string;
  attributes: { [key: string]: string };
  children: Array<TEIElement | TEIText | TEIComment>;
  parent?: TEIElement | null; // optional parent reference to ease navigation
}

export interface TEIText {
  type: "Text";
  text: string;
  parent?: TEIElement | null;
}

export interface TEIComment {
  type: "Comment";
  text: string;
  parent?: TEIElement | null;
}

export type TEINode = TEIElement | TEIText | TEIComment;
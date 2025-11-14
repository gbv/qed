import {type TEIElement, type TEINode, type TEIText, type TEIComment} from "~/api/tei.model";

export const useTei = () => {

  let parser: any;

  if(import.meta.server) {
    const dom = new (useNuxtApp().$jsdom)(`<!DOCTYPE html><body></body>`);
    parser = new dom.window.DOMParser();
  } else {
    parser = new DOMParser();
  }

  /**
   * Parse a TEI/XML string into a tree of TEINode objects.
   * Uses DOMParser (browser). For Node environments consider providing a DOM implementation.
   * Returns the top-level element nodes (usually a single <TEI> element) as an array.
   */
  function parseTEI(xml: string): TEIElement[] {
    let doc: Document;
    doc = parser.parseFromString(xml, 'application/xml');

    // Fallback node type constants if global Node is not present
    const NODE = (typeof Node !== 'undefined')
      ? Node
      : { ELEMENT_NODE: 1, TEXT_NODE: 3, COMMENT_NODE: 8 } as any;

    function convertNode(node: Node, parent: TEIElement | null): TEINode | null {
      switch (node.nodeType) {
        case NODE.ELEMENT_NODE: {
          const el = node as Element;
          const attrs: { [key: string]: string } = {};
          for (let i = 0; i < el.attributes.length; i++) {
            const a = el.attributes.item(i)!;
            attrs[a.name.replaceAll(':', '-')] = a.value;
          }
          const teiEl: TEIElement = {
            type: "Element",
            name: el.tagName,
            attributes: attrs,
            children: [],
            parent,
          };
          // children
          for (let i = 0; i < el.childNodes.length; i++) {
            const childNode = convertNode(el.childNodes[i], teiEl);
            if (childNode) teiEl.children.push(childNode as any);
          }
          return teiEl;
        }
        case NODE.TEXT_NODE: {
          const text = node.textContent || "";
          // skip pure whitespace text nodes
          if (/^\s*$/.test(text)) return null;
          const t: TEIText = {
            type: "Text",
            text,
            parent,
          };
          return t;
        }
        case NODE.COMMENT_NODE: {
          const c: TEIComment = {
            type: "Comment",
            text: node.textContent || "",
            parent,
          };
          return c;
        }
        default:
          return null; // ignore other node types
      }
    }

    const results: TEIElement[] = [];
    for (let i = 0; i < doc.childNodes.length; i++) {
      const n = doc.childNodes[i];
      const converted = convertNode(n, null);
      if (converted && converted.type === "Element") results.push(converted as TEIElement);
    }
    return results;
  }

  /**
   * A small jQuery-like wrapper around TEIElement nodes for querying and navigation.
   * Usage: const $nodes = $tei(xmlString) or $tei(parsedElements)
   */
  class TeiQuery {
    private nodes: TEINode[];

    constructor(nodes: TEINode[] | TEINode) {
      this.nodes = Array.isArray(nodes) ? nodes : [nodes];
    }

    // internal helper to flatten and unique elements
    private unique(nodes: TEINode[]): TEINode[] {
      const seen = new Set<TEINode>();
      const res: TEINode[] = [];
      for (const n of nodes) {
        if (!seen.has(n)) {
          seen.add(n);
          res.push(n);
        }
      }
      return res;
    }

    // find descendants by tag name or predicate
    find(nameOrPredicate: string | ((el: TEIElement) => boolean)): TeiQuery {
      const predicate =
        typeof nameOrPredicate === "string"
          ? (el: TEIElement) => el.name === nameOrPredicate
          : nameOrPredicate;
      const out: TEIElement[] = [];
      function walk(el: TEIElement) {
        for (const c of el.children) {
          if (c.type === "Element") {
            if (predicate(c)) out.push(c);
            walk(c);
          }
        }
      }
      for (const n of this.nodes) if (n.type === 'Element') walk(n);
      return new TeiQuery(this.unique(out));
    }

    // children elements (direct)
    // children returns all direct child nodes, including Text and Comment nodes
    children(): TeiQuery {
      const out: TEINode[] = [];
      for (const n of this.nodes) {
        if (n.type === 'Element') {
          for (const c of n.children) out.push(c);
        }
      }
      return new TeiQuery(this.unique(out));
    }

    // parent elements (direct)
    parent(): TeiQuery {
      const out: TEIElement[] = [];
      for (const n of this.nodes) {
        if (n.type === 'Element' && n.parent) out.push(n.parent as TEIElement);
        else if ((n as any).parent) out.push((n as any).parent as TEIElement);
      }
      return new TeiQuery(this.unique(out));
    }

    // return the top-most root elements for the current nodes
    root(): TeiQuery {
      const roots: TEIElement[] = [];
      for (const n of this.nodes) {
        // start at element node or its parent if it's a text/comment
        let cur: any = n;
        if (cur.type !== 'Element') cur = (cur as any).parent;
        // walk up to the top
        while (cur && (cur as any).parent) cur = (cur as any).parent;
        if (cur && cur.type === 'Element') roots.push(cur as TEIElement);
      }
      return new TeiQuery(this.unique(roots));
    }

    // find elements by id (xml:id or id attribute) within current nodes' subtrees
    id(idValue: string): TeiQuery {
      const out: TEIElement[] = [];
      function walk(el: TEIElement) {
        if (el.attributes['xml-id'] === idValue || el.attributes['id'] === idValue) out.push(el);
        for (const c of el.children) if (c.type === 'Element') walk(c as TEIElement);
      }
      for (const n of this.nodes) {
        if (n.type === 'Element') walk(n as TEIElement);
        else if ((n as any).parent) walk((n as any).parent as TEIElement);
      }
      return new TeiQuery(this.unique(out));
    }

    // get attribute value from first node or all attributes if no name
    attr(name?: string): string | Record<string, string> | undefined {
      if (this.nodes.length === 0) return undefined;
      const first = this.nodes[0];
      if (first.type !== 'Element') return undefined;
      if (name) return (first as TEIElement).attributes[name];
      return { ...(first as TEIElement).attributes };
    }

    // set attribute on all nodes
    setAttr(name: string, value: string): this {
      for (const n of this.nodes) if (n.type === 'Element') (n as TEIElement).attributes[name] = value;
      return this;
    }

    // text content (concatenated text nodes under elements)
    text(): string {
      function collectNode(n: TEINode): string {
        if (n.type === 'Text') return n.text;
        if (n.type === 'Comment') return '';
        // Element
        let s = '';
        for (const c of (n as TEIElement).children) s += collectNode(c);
        return s;
      }
      return this.nodes.map(collectNode).join('');
    }

    // filter by name or predicate
    filter(nameOrPredicate: string | ((node: TEINode) => boolean)): TeiQuery {
      if (typeof nameOrPredicate === 'string') {
        return new TeiQuery(this.nodes.filter(n => n.type === 'Element' && (n as TEIElement).name === nameOrPredicate));
      }
      return new TeiQuery(this.nodes.filter(nameOrPredicate));
    }

    // iterate
    each(fn: (node: TEINode, idx: number) => void): this {
      this.nodes.forEach((n, i) => fn(n, i));
      return this;
    }

    map<T>(fn: (node: TEINode, idx: number) => T): T[] {
      return this.nodes.map(fn);
    }

    // handy accessors
    toArray(): TEINode[] {
      return [...this.nodes];
    }

    // returns number of matched nodes
    count(): number {
      return this.nodes.length;
    }

    // return only text nodes
    textNodes(): TeiQuery {
      return new TeiQuery(this.nodes.filter(n => n.type === 'Text'));
    }

    // return a string per current node: for Text nodes the text, for Element nodes the concatenated child text
    texts(): string[] {
      function nodeToText(n: TEINode): string {
        if (n.type === 'Text') return n.text;
        if (n.type === 'Comment') return '';
        // Element
        let s = '';
        for (const c of (n as TEIElement).children) {
          if (c.type === 'Text') s += c.text;
          else if (c.type === 'Element') s += nodeToText(c);
        }
        return s;
      }
      return this.nodes.map(nodeToText);
    }

    first(): TeiQuery {
      return new TeiQuery(this.nodes.length ? this.nodes[0] : [] as any);
    }

    last(): TeiQuery {
      return new TeiQuery(this.nodes.length ? this.nodes[this.nodes.length - 1] : [] as any);
    }

    // convenience: get underlying raw node
    get(index = 0): TEINode | undefined {
      return this.nodes[index];
    }
  }

  /**
   * Factory function. Accepts XML string, Document, TEIElement or array of TEIElement.
   */
  function $tei(input: string | Document | TEINode | TEINode[]): TeiQuery {
    // If input is a string, parse it (browser or jsdom-aware)
    if (typeof input === "string") {
      const parsed = parseTEI(input);
      return new TeiQuery(parsed);
    }

    // Document detection: support browser Document and jsdom Document (nodeType === 9)
    if (input && typeof (input as any).nodeType === 'number' && (input as any).nodeType === 9) {
      const doc = input as Document;
      const els: TEIElement[] = [];
      // Serialize top-level element children into TEIElement via parseTEI
      for (let i = 0; i < doc.childNodes.length; i++) {
        const n = doc.childNodes[i];
        if (n.nodeType === (typeof Node !== 'undefined' ? Node.ELEMENT_NODE : 1)) {
          // Prefer global XMLSerializer, otherwise try using the Document's defaultView (jsdom window)
          let serializer: any = null;
          if (typeof XMLSerializer !== 'undefined') serializer = new XMLSerializer();
          else if ((doc as any).defaultView && (doc as any).defaultView.XMLSerializer) {
            serializer = new (doc as any).defaultView.XMLSerializer();
          }

          if (serializer && typeof serializer.serializeToString === 'function') {
            els.push(parseTEI(serializer.serializeToString(n))[0]);
          } else {
            // Fallback: use outerHTML/textContent where available
            const xmlStr = (n as any).outerHTML || (n as any).toString();
            els.push(parseTEI(xmlStr)[0]);
          }
        }
      }
      return new TeiQuery(els);
    }

    if (Array.isArray(input)) return new TeiQuery(input);
    return new TeiQuery(input as TEINode);
  }

  return { $tei };
}




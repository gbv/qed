export async function XMLApi(xmlStr: string) {
    if (process.server) {
        const jsdom = await import("jsdom");
        const dom = new jsdom.JSDOM(`<!DOCTYPE html><body></body>`);
        const parser = new dom.window.DOMParser();
        const xml = parser.parseFromString(xmlStr, "text/xml");
        return XMLApiXML(xml.firstChild);
    } else {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlStr, "text/xml");
        return XMLApiXML(xml.firstChild);
    }
}

export function XMLApiXML(xml: Node): XElement {
    const result: XElement = {name: xml.nodeName, attributes: {}};
    result.content = Array.from(xml.childNodes)
        .filter(n => {
            return n.nodeType === 1 || n.nodeType === 3
        })
        .map(n => n.nodeType === 1 ? XMLApiXML(n) : n.textContent);


    const elem = xml as Element;
    for (let i = 0; i < elem.attributes.length; i++) {
        const cn = elem.attributes.item(i);
        if (cn.nodeType === 2) {
            const attr: Attr = cn as Attr;
            result.attributes[attr.nodeName] = attr.value;
        }
    }

    return result;
}


export interface XElement {
    name: string,
    attributes?: Record<string, string>,
    content?: Array<XElement | String>
}

export interface Filter<T> {
    (obj: T): boolean;
}

function isElement(content: XElement | String) {
    return content instanceof Object && "name" in content;
}

export function findFirstElement(parent: XElement, filterFn: Filter<XElement>): XElement | null {
    for (const content of parent.content) {
        if (isElement(content)) {
            const elemContent = content as XElement;
            if (filterFn(elemContent)) {
                return elemContent;
            } else {
                const element = findFirstElement(elemContent, filterFn);
                if (element != null) {
                    return element;
                }
            }
        }
    }
    return null;
}

export function findElement(parent: XElement, filterFn: Filter<XElement>, result = new Array<XElement>()): Array<XElement> {
    for (const content of parent.content) {
        if (isElement(content)) {
            const elemContent = content as XElement;
            if (filterFn(elemContent)) {
                result.push(elemContent);
            } else {
                findElement(elemContent, filterFn, result);
            }
        }
    }
    return result;
}

export function byAttr(attrName: string, attrValue?: string): Filter<XElement> {
    return attrValue == undefined ? (elem) => attrName in elem.attributes : (elem) => attrName in elem.attributes && elem.attributes[attrName] === attrValue;
}

export function byName(name: string): Filter<XElement> {
    return (elem) => elem.name === name;
}

export function and<T>(...filters: Array<Filter<T>>) {
    return (elem: T) => {
        for (const filter of filters) {
            if (!filter(elem)) {
                return false;
            }
        }
        return true;
    }
}

export function or<T>(...filters: Array<Filter<T>>) {
    return (elem: T) => {
        for (const filter of filters) {
            if (filter(elem)) {
                return true;
            }
        }
    }
}

export function flattenElement(parent: XElement | string) {
    const array = new Array<string>();
    flattenElementBuilder(parent, array)
    return array.join("");
}

function flattenElementBuilder(parent: XElement | string, builder = new Array<string>()) {
    if (!isElement(parent)) {
        return parent;
    } else {
        for (const content of (parent as XElement).content) {
            if (isElement(content)) {
                flattenElementBuilder(content as XElement, builder);
            } else {
                builder.push(content as string);
            }
        }
    }
}

export function flattenElementExcept(parent: XElement | string, filter: Filter<XElement>) {
    const arr = new Array<string | XElement>()

    flattenElementExceptBuilder(parent, arr, filter);
    console.log(["Elements", ...arr]);

    return arr;
}

function flattenElementExceptBuilder(parent: XElement | string, builder = new Array<string | XElement>(), filter: Filter<XElement>) {
    if (!isElement(parent)) {
        return parent;
    } else {
        for (const content of (parent as XElement).content) {
            if (isElement(content)) {
                if(filter(content as XElement)){
                    builder.push(content as XElement);
                } else {
                    flattenElementExceptBuilder(content as XElement, builder, filter);
                }
            } else {
                builder.push(content as string);
            }
        }
    }
}

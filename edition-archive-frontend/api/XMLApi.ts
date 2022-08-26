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
        .map(n => n.nodeType === 1 ? XMLApiXML(n) : { text: n.textContent});


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
    content?: Array<XElement | XText>
}

export  interface XText {
    text:string;
}

export interface Filter<T> {
    (obj: T): boolean;
}

function isElement(content: XElement | XText) {
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

export function flattenElement(parent: XElement | XText) {
    const array = new Array<string>();
    flattenElementBuilder(parent, array)
    const result = array.join("");
    //console.log(["FLATTEN", parent, "FLATTEN TO", result])

    return result;

}

function flattenElementBuilder(parent: XElement | XText, builder = new Array<string>()) {
    if (!isElement(parent)) {
        builder.push((parent as XText).text);
    } else {
        for (const content of (parent as XElement).content) {
            if (isElement(content)) {
                flattenElementBuilder(content as XElement, builder);
            } else {
                builder.push((content as XText).text);
            }
        }
    }
}

export function flattenElementExcept(parent: XElement | XText, filter: Filter<XElement>) {
    const arr = new Array<string | XElement>()

    flattenElementExceptBuilder(parent, arr, filter);

    return arr;
}

function flattenElementExceptBuilder(parent: XElement | XText, builder = new Array<string | XElement>(), filter: Filter<XElement>) {
    if (!isElement(parent)) {
        builder.push((parent as XText).text);
    } else {
        for (const content of (parent as XElement).content) {
            if (isElement(content)) {
                if(filter(content as XElement)){
                    builder.push(content as XElement);
                } else {
                    flattenElementExceptBuilder(content as XElement, builder, filter);
                }
            } else {
                builder.push((content as XText).text);
            }
        }
    }
}

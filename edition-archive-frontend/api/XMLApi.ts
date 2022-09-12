import {dom2XElement} from "@mycore-org/xml-json-api"

export async function XMLApi(xmlStr: string) {
    if (process.server) {
        const jsdom = await import("jsdom");
        const dom = new jsdom.JSDOM(`<!DOCTYPE html><body></body>`);
        const parser = new dom.window.DOMParser();
        const xml = parser.parseFromString(xmlStr, "text/xml");
        return dom2XElement(xml.firstChild as Element);
    } else {
        const parser = new DOMParser();
        const xml = parser.parseFromString(xmlStr, "text/xml");
        return dom2XElement(xml.firstChild as Element);
    }
}

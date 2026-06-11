/**
 * Helpers for the LOD start-page map.
 *
 * Coordinates for LOD live on the TEI files (objectType:data_file), not on the
 * mods objects: each data_file carries a `ditav.tei.coordinates` WKT
 * GEOMETRYCOLLECTION and a `returnId` pointing back to its mods object. The map
 * is therefore built from the data_file docs, and the object docs (titles etc.)
 * are fetched lazily by returnId when a cluster is selected.
 */

/**
 * Builds the solr URL that returns all published LOD data_file docs carrying
 * coordinates, with just the fields the map needs.
 */
export function buildLodMapDataFileURL(ditavSolrURL: string): string {
  const url = new URL(ditavSolrURL);
  url.search = '';
  url.searchParams.set('q', 'objectType:data_file AND objectProject:lod AND state:published');
  url.searchParams.set('fq', 'ditav.tei.coordinates.str:*');
  url.searchParams.set('fl', 'returnId,ditav.tei.coordinates');
  url.searchParams.set('rows', '99999');
  url.searchParams.set('wt', 'json');
  return url.toString();
}

/**
 * Builds the solr URL that fetches the display fields for the given mods object ids.
 */
export function buildLodObjectsURL(ditavSolrURL: string, ids: string[]): string {
  const url = new URL(ditavSolrURL);
  url.search = '';
  const idQuery = ids.map((id) => `"${id}"`).join(' OR ');
  url.searchParams.set('q', `id:(${idQuery})`);
  url.searchParams.set('fl',
    'id,derCount,ditav.mods.title.lang.en,mods.title.main,mods.title,ditav.mods.author.facet,ditav.mods.recipient.facet');
  url.searchParams.set('rows', ids.length.toString());
  url.searchParams.set('wt', 'json');
  return url.toString();
}

/**
 * Extracts a display title from a LOD mods solr doc, mirroring the precedence used
 * by the LOD search result list.
 */
export function lodDocumentTitle(doc: any): string {
  return doc?.['ditav.mods.title.lang.en']?.[0]
    || doc?.['mods.title.main']
    || doc?.['mods.title']?.[0]
    || doc?.id;
}

/**
 * Builds an "author → recipient" subtitle from a LOD mods solr doc, or an empty
 * string when neither is present.
 */
export function lodDocumentSubtitle(doc: any): string {
  const author = doc?.['ditav.mods.author.facet']?.[0];
  const recipient = doc?.['ditav.mods.recipient.facet']?.[0];
  if (author && recipient) {
    return `${author} → ${recipient}`;
  }
  return author || recipient || '';
}

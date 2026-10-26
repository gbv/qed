export function buildSearchQuery(queryTerm: string): string {
  return queryTerm === '*' ? '*' : queryTerm.replace(/^"|"$/g, '');
}

export function useMyCoReFindEndpoint(url: URL): void {
  if (import.meta.client) {
    url.href = new URL('/api/ditav-search', window.location.origin).toString();
  } else {
    url.pathname = url.pathname.replace(/\/api\/v1\/search\/?$/, '/servlets/solr/find');
  }
}
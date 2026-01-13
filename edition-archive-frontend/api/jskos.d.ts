export type URI = string;


export type LangMap = Record<string, string | string[]>;

export type DateString = string;


export interface JSKOSEntity {
  uri?: URI;
  type?: URI[];

  created?: DateString;
  issued?: DateString;
  modified?: DateString;

  creator?: URI[];
  contributor?: URI[];
  publisher?: URI[];

  partOf?: URI[];

  url?: URI;
  identifier?: string[];

  notation?: string[];

  prefLabel?: LangMap;
  altLabel?: LangMap;
  hiddenLabel?: LangMap;

  note?: LangMap;
  scopeNote?: LangMap;
  definition?: LangMap;
  example?: LangMap;
  historyNote?: LangMap;
  editorialNote?: LangMap;
  changeNote?: LangMap;

  subject?: URI[];
  subjectOf?: URI[];

  source?: URI[];

  depiction?: URI[];
  media?: any; // IIIF resource, very heterogeneous

  place?: URI[];
  startPlace?: URI[];
  endPlace?: URI[];

  narrower?: URI[];
  broader?: URI[];
  related?: URI[];

  previous?: URI[];
  next?: URI[];

  startDate?: DateString;
  endDate?: DateString;
  relatedDate?: URI;
  relatedDates?: URI;

  location?: any; // GeoJSON

  address?: string;
  street?: string;
  ext?: string;
  pobox?: string;
  locality?: string;
  region?: string;
  code?: string;
  country?: string;

  ancestors?: URI[];
  inScheme?: URI[];
  topConceptOf?: URI[];
  topConcepts?: URI[];
  concepts?: URI[];

  versionOf?: URI[];

  extent?: string;
  languages?: string[];
  license?: URI[];

  deprecated?: boolean;
  replacedBy?: URI;

  namespace?: string;
  uriPattern?: string;

  fromScheme?: URI;
  toScheme?: URI;

  memberList?: URI[];
  memberSet?: URI[];
  memberChoice?: URI[];

  count?: number;

  distributions?: JSKOSDistribution[];
  services?: JSKOSService[];

  download?: URI;
  accessURL?: URI;

  checksum?: URI;
  mimetype?: string;
  packageFormat?: string;
  compressFormat?: string;

  format?: string;
  size?: number;

  value?: string;

  // nested qualified relations/literals/dates
  qualifiedRelations?: Record<string, unknown>;
  qualifiedLiterals?: Record<string, unknown>;
  qualifiedDates?: Record<string, unknown>;

  resource?: URI;
  date?: URI;
  literal?: JSKOSLiteral;

  rank?: string;
  version?: string;

  justification?: URI;
  tool?: string;
  issue?: URI;
  issueTracker?: URI;

  guidelines?: URI;
  api?: URI;
  endpoint?: URI;

  serves?: URI[];
}

/**
 * Literal as SKOS-XL structure
 */
export interface JSKOSLiteral {
  string?: string;
  language?: string;
}

/**
 * DCAT distribution
 */
export interface JSKOSDistribution {
  download?: URI;
  accessURL?: URI;
  mimetype?: string;
  size?: number;
  format?: string;
  checksum?: URI;
  value?: string;
}

/**
 * DCAT service
 */
export interface JSKOSService {
  endpoint?: URI;
  mimetype?: string;
  format?: string;
  accessURL?: URI;
}

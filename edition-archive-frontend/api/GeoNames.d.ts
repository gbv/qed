export interface GeoNamesEntry {
  timezone: GeoNamesTimezone
  bbox: GeoNamesBoundingBox

  asciiName: string
  toponymName: string
  name: string

  lat: string
  lng: string

  geonameId: number

  countryId: string
  countryCode: string
  countryName: string
  continentCode: string

  fcl: string
  fclName: string
  fcode: string
  fcodeName: string

  population: number

  wikipediaURL?: string

  srtm3?: number
  astergdem?: number

  adminCode1?: string
  adminId1?: string

  adminCodes1?: {
    ISO3166_2?: string
    [key: string]: string | undefined
  }

  adminName1?: string
  adminName2?: string
  adminName3?: string
  adminName4?: string
  adminName5?: string

  alternateNames: GeoNamesAlternateName[]
}

export interface GeoNamesTimezone {
  timeZoneId: string
  gmtOffset: number
  dstOffset: number
}

export interface GeoNamesBoundingBox {
  east: number
  west: number
  north: number
  south: number
  accuracyLevel?: number
}

export interface GeoNamesAlternateName {
  name: string

  lang?: string

  isShortName?: boolean
  isPreferredName?: boolean
  isColloquial?: boolean
  isHistoric?: boolean

  from?: string
  to?: string
}
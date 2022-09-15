<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                version="3.0" exclude-result-prefixes="xlink">
    <xsl:import href="xslImport:solr-document-3:solr/searchfields-solr.xsl"/>

    <xsl:template match="mycoreobject">
        <xsl:apply-imports/>

    </xsl:template>

</xsl:stylesheet>
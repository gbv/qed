<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                version="3.0" exclude-result-prefixes="xlink">
    <xsl:import href="xslImport:solr-document-3:solr/searchfields-solr.xsl"/>

    <xsl:template match="mycoreobject">
        <xsl:apply-imports/>

        <xsl:apply-templates select="metadata/def.regest/regest"/>
    </xsl:template>

    <xsl:template match="regest">
        <xsl:variable name="json" select="text()"/>
        <xsl:apply-templates select="fn:json-to-xml($json)" mode="regest"/>
    </xsl:template>

    <xsl:template match="fn:map" mode="regest">

        <xsl:apply-templates select="fn:string[@key='idno']" mode="regest" />
        <xsl:apply-templates select="fn:map[@key='issued']" mode="regest"/>
        <xsl:apply-templates select="fn:string[@key='issuer']" mode="regest"/>
        <xsl:apply-templates select="fn:string[@key='recipient']" mode="regest"/>
        <xsl:apply-templates select="fn:string[@key='initium']" mode="regest"/>
        <xsl:apply-templates select="fn:string[@key='issuedPlace']" mode="regest"/>
        <xsl:apply-templates select="fn:map[@key='authenticityStatus']" mode="regest"/>
    </xsl:template>

    <xsl:template match="fn:string[@key='idno']" mode="regest">
        <field name="idno">
            <xsl:value-of select="text()"/>
        </field>
    </xsl:template>

    <xsl:template match="fn:string[@key='issuer']" mode="regest">
        <field name="issuer">
            <xsl:value-of select="text()"/>
        </field>
    </xsl:template>

    <xsl:template match="fn:string[@key='initium']" mode="regest">
        <field name="initium">
            <xsl:value-of select="text()" />
        </field>
    </xsl:template>

    <xsl:template match="fn:string[@key='issuedPlace']" mode="regest">
        <field name="issuedPlace">
            <xsl:value-of select="text()" />
        </field>
    </xsl:template>

    <xsl:template match="fn:string[@key='recipient']" mode="regest">
        <field name="recipient">
            <xsl:value-of select="text()"/>
        </field>
    </xsl:template>

    <xsl:template match="fn:map[@key='authenticityStatus']" mode="regest">
        <field name="fake">
            <xsl:value-of select="fn:boolean[@key='fake']" />
        </field>
        <field name="lost">
            <xsl:value-of select="fn:boolean[@key='lost']" />
        </field>
        <field name="certainly">
            <xsl:value-of select="fn:boolean[@key='certainly']" />
        </field>
    </xsl:template>

    <xsl:template match="fn:map[@key='issued']" mode="regest">
        <xsl:if test="fn:string[@key='text']">
            <field name="issued.text">
                <xsl:value-of select="fn:string[@key='text']"/>
            </field>
        </xsl:if>
        <xsl:choose>
            <xsl:when test="count(fn:string[@key='from'])&gt;0 and count(fn:string[@key='to'])&gt;0">
                <xsl:variable name="from" select="substring-before(fn:string[@key='from'],'T')"/>
                <xsl:variable name="to" select="substring-before(fn:string[@key='to'],'T')"/>
                <field name="issued.range">
                    [<xsl:value-of select="$from"/> TO <xsl:value-of select="$to"/>]
                </field>
            </xsl:when>
            <xsl:when test="count(fn:string[@key='from'])&gt;0">
                <xsl:variable name="from" select="substring-before(fn:string[@key='from'], 'T')"/>
                <field name="issued.range">
                    [<xsl:value-of select="$from"/> TO <xsl:value-of select="$from"/>]
                </field>
            </xsl:when>
            <xsl:when test="count(fn:string[@key='to'])&gt;0">
                <xsl:variable name="to" select="substring-before(fn:string[@key='to'], 'T')"/>
                <field name="issued.range">
                    [<xsl:value-of select="$to"/> TO<xsl:value-of select="$to"/>]
                </field>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
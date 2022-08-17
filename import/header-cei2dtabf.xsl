<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:cei="http://www.monasterium.net/NS/cei"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                version="2.0"
                exclude-result-prefixes="xs xlink">

    <!-- old file which endet up being not used -->
    <xsl:output cdata-section-elements="description" method="xml"/>

    <xsl:template match="/cei:cei">
        <tei:TEI>
            <xsl:apply-templates select="cei:teiHeader"/>
        </tei:TEI>
    </xsl:template>

    <xsl:template match="cei:teiHeader">
        <tei:teiHeader>
            <xsl:apply-templates/>
        </tei:teiHeader>
    </xsl:template>

    <xsl:template match="cei:fileDesc">
        <tei:fileDesc>
            <xsl:apply-templates/>
            <!-- Move Content from ../encodingDesc to sourceDesc -->
            <xsl:apply-templates select="../cei:encodingDesc" mode="copied"/>
        </tei:fileDesc>
    </xsl:template>

    <xsl:template match="cei:*">
        <xsl:element name="tei:{local-name()}">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cei:p[@type='editionStmt']">
        <xsl:apply-templates select="cei:note[@type='volume']"/>
    </xsl:template>

    <xsl:template match="cei:note[@type='volume']">
        <tei:title type="volume">
            <xsl:value-of select="text()"/>
        </tei:title>
    </xsl:template>

    <xsl:template match="cei:p[@type='editor']">
        <tei:editor>
            <tei:persName>
                <tei:surname>
                    <xsl:value-of select="substring-after(text(), ' ')"/>
                </tei:surname>
                <tei:forename>
                    <xsl:value-of select="substring-before(text(), ' ')"/>
                </tei:forename>
            </tei:persName>
        </tei:editor>
    </xsl:template>

    <xsl:template match="cei:encodingDesc">

    </xsl:template>

    <xsl:template match="cei:encodingDesc" mode="copied">
        <tei:sourceDesc>
            <tei:biblFull>
                <tei:publicationStmt>
                    <xsl:
                </tei:publicationStmt>
            </tei:biblFull>
        </tei:sourceDesc>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="cei:sourceDesc">
        <!-- remove because its empty in the examples -->
    </xsl:template>


</xsl:stylesheet>
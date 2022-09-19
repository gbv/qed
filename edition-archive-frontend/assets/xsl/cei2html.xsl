<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="3.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:cei="http://www.monasterium.net/NS/cei"
  exclude-result-prefixes="cei">

  <xsl:output method="html" encoding="utf-8" indent="yes" />
  <xsl:strip-space elements="*" />

  <!--
  <xsl:template match="/">
    <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
    <html>
      <head>
        <title>Test</title>
      </head>
      <body>
        <h1>Gallia Pontificia online</h1>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  -->
<!-- Global -->

  <xsl:template match="/">
    <div>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="cei:text">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="cei:foreign[@lang='lat']">
    <span style="font-style: italic;">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>
  
  <xsl:template match="cei:sup">
    <sup>
      <xsl:value-of select="."/>
    </sup>
  </xsl:template>

  <xsl:template match="cei:cei/cei:teiHeader">
    <!-- hier gib's nix zu sehen -->
  </xsl:template>
  
  <xsl:template match="cei:cei/cei:text/cei:group">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="cei:text/cei:body">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="cei:idno">
    <h2 style="text-align: center;"><xsl:value-of select="."/></h2>
  </xsl:template>
  
  <xsl:template match="cei:chDesc">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="cei:issued">
    <p style="font-weight:bold;text-align: center;"><xsl:value-of select="."/></p>
  </xsl:template>
  
  <xsl:template match="cei:abstract">
    <p>
      <xsl:apply-templates />
      <xsl:apply-templates select="../cei:diplomaticAnalysis/cei:incipit" />
    </p>
  </xsl:template>
  
  <xsl:template match="cei:issuer">
    <span style="letter-spacing: .2em">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>
  
  <xsl:template match="cei:recipient">
    <span style="letter-spacing: .2em">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>
  
  <xsl:template match="cei:persName">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="listBiblRegest">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="cei:incipit">
    <xsl:text> - </xsl:text>
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="cei:diplomaticAnalysis">
    <xsl:apply-templates select="node()[not(cei:incipit)]" />
  </xsl:template>
  
  <xsl:template match="cei:p[@type]">
    <div>
      <h3><xsl:value-of select="@type" /></h3>
      <xsl:apply-templates />
    </div>
  </xsl:template>
  
  <xsl:template match="cei:listBiblRegest">
    <div>
      <h3><xsl:value-of select="substring-before(.,':')" /></h3>
      <xsl:apply-templates />
    </div>
  </xsl:template>
  
  <xsl:template match="cei:listBiblEdition">
    <p style="padding-left:20px;">
      <xsl:apply-templates />
    </p>
  </xsl:template>
  
  <xsl:template match="cei:bibl">
    <xsl:apply-templates />
  </xsl:template>
 
  <xsl:template match="cei:author[not(@rend='medieval')]">
    <span style="font-variant: small-caps;"><xsl:value-of select="." /></span>
  </xsl:template>
  
  <xsl:template match="cei:ref">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="cei:date">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="cei:dateRange">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="cei:hi">
    <span style="background-color: #FFFF00"><xsl:value-of select="." /></span>
  </xsl:template>
  
  <xsl:template match="cei:witListPar2">
    <p style="padding-left:20px;">
      <xsl:for-each select="*">
        <span>
          <xsl:apply-templates />
        </span>
      </xsl:for-each>
    </p>
  </xsl:template>
  
  <xsl:template match="cei:head2">
    <br></br>
    <xsl:value-of select="." />    
  </xsl:template>
 
  <xsl:template match="cei:witness2">
    <xsl:value-of select="." /> 
  </xsl:template>

  <xsl:template match="cei:witListPar">
    <p style="padding-left:20px;">
      <xsl:for-each select=".">
        <span style="display:block;"><xsl:value-of select="."/></span>
      </xsl:for-each>
    </p>
  </xsl:template>

  <xsl:template match="cei:witnessOrig">
    <p style="padding-left:20px; ">
      <xsl:value-of select="." />
    </p>
  </xsl:template>
     
  <xsl:template match="cei:msIdentifier">
    <span style="padding-left:10px;">
      <xsl:value-of select="." />
    </span> 
  </xsl:template>
  
  <xsl:template match="cei:placeName">
    <xsl:apply-templates />    
  </xsl:template>

  <xsl:template match="cei:note">
    <xsl:apply-templates />
  </xsl:template>

 </xsl:stylesheet>

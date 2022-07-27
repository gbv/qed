<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation" exclude-result-prefixes="i18n">
    <xsl:variable name="Type" select="'document'" />

    <xsl:variable name="PageTitle" select="i18n:translate('titles.pageTitle.error',concat(' ',/mcr_error/@HttpError))" />


    <xsl:template match="/mcr_error[contains('401|403', @HttpError)]">
        <xsl:call-template name="printNotLoggedIn"/>
    </xsl:template>


    <xsl:include href="MyCoReLayout.xsl" />
</xsl:stylesheet>
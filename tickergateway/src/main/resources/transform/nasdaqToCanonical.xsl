<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:nas="http://www.nasdaq.com/services/"
        exclude-result-prefixes="xsi nas">
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" undeclare-prefixes="yes"/>

    <xsl:template match="/">
            <xsl:apply-templates select="//nas:Feed"/>
    </xsl:template>

    <xsl:template match="nas:Feed">
    	<stockTicker xmlns="http://mycorp.stocks.com">
			<time><xsl:value-of select="./@timestamp"/></time>
			<stockName><xsl:value-of select="./@symbol"/></stockName>
			<price>
			<xsl:attribute name="currency"><xsl:value-of select="./@currency"></xsl:value-of></xsl:attribute>
				<current><xsl:value-of select="./nas:current"/></current>
			</price>
		</stockTicker>
    </xsl:template>
</xsl:stylesheet>
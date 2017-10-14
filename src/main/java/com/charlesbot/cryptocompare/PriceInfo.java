package com.charlesbot.cryptocompare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceInfo {
	/* "FROMSYMBOL": "Ξ",
        "TOSYMBOL": "$",
        "MARKET": "CryptoCompare Index",
        "PRICE": "$ 339.01",
        "LASTUPDATE": "Just now",
        "LASTVOLUME": "Ξ 0.01328",
        "LASTVOLUMETO": "$ 4.49",
        "LASTTRADEID": "1952602434",
        "VOLUME24HOUR": "Ξ 592,550.7",
        "VOLUME24HOURTO": "$ 201,603,286.2",
        "OPEN24HOUR": "$ 329.31",
        "HIGH24HOUR": "$ 348.29",
        "LOW24HOUR": "$ 328.8",
        "LASTMARKET": "Gemini",
        "CHANGE24HOUR": "$ 9.7",
        "CHANGEPCT24HOUR": "2.95",
        "SUPPLY": "Ξ 95,100,621.1",
        "MKTCAP": "$ 32.24 B"
        */
	
	public String fromCode;
	public String toCode;
	
	@JsonProperty("FROMSYMBOL")
	public String fromSymbol;
	
	@JsonProperty("TOSYMBOL")
	public String toSymbol;
	
	@JsonProperty("MARKET")
	public String market;
	
	@JsonProperty("PRICE")
	public String price;
	
	@JsonProperty("LASTUPDATE")
	public String lastUpdate;
	
	@JsonProperty("OPEN24HOUR")
	public String open24Hour;
		
	@JsonProperty("HIGH24HOUR")
	public String high24Hour;
	
	@JsonProperty("LOW24HOUR")
	public String low24Hour;
	
	@JsonProperty("CHANGE24HOUR")
	public String change24Hour;
	
	@JsonProperty("CHANGEPCT24HOUR")
	public String changePercent24Hour;
	
	@JsonProperty("SUPPLY")
	public String supply;
	
	@JsonProperty("MKTCAP")
	public String marketCap;

	@Override
	public String toString() {
		return "PriceInfo [fromSymbol=" + fromSymbol + ", toSymbol=" + toSymbol + ", market=" + market + ", price="
				+ price + ", lastUpdate=" + lastUpdate + ", open24Hour=" + open24Hour + ", high24Hour=" + high24Hour
				+ ", low24Hour=" + low24Hour + ", change24Hour=" + change24Hour + ", changePercent24Hour="
				+ changePercent24Hour + ", supply=" + supply + ", marketCap=" + marketCap + "]";
	}
	
	
	
	
	
}

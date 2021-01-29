package com.charlesbot.iex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {

	/*
	{
	  "symbol": "AAPL",
	  "companyName": "Apple Inc.",
	  "primaryExchange": "Nasdaq Global Select",
	  "sector": "Technology",
	  "calculationPrice": "tops",
	  "open": 154,
	  "openTime": 1506605400394,
	  "close": 153.28,
	  "closeTime": 1506605400394,
	  "latestPrice": 158.73,
	  "latestSource": "Previous close",
	  "latestTime": "September 19, 2017",
	  "latestUpdate": 1505779200000,
	  "latestVolume": 20567140,
	  "iexRealtimePrice": 158.71,
	  "iexRealtimeSize": 100,
	  "iexLastUpdated": 1505851198059,
	  "delayedPrice": 158.71,
	  "delayedPriceTime": 1505854782437,
	  "previousClose": 158.73,
	  "change": -1.67,
	  "changePercent": -0.01158,
	  "iexMarketPercent": 0.00948,
	  "iexVolume": 82451,
	  "avgTotalVolume": 29623234,
	  "iexBidPrice": 153.01,
	  "iexBidSize": 100,
	  "iexAskPrice": 158.66,
	  "iexAskSize": 100,
	  "marketCap": 751627174400,
	  "peRatio": 16.86,
	  "week52High": 159.65,
	  "week52Low": 93.63,
	  "ytdChange": 0.3665,
	}
	*/
	@JsonProperty
	public String symbol;
	
	@JsonProperty
	public String companyName;
	
	@JsonProperty
	public String sector;
	
	@JsonProperty
	public BigDecimal latestPrice;
	
	@JsonProperty
	public Long latestUpdate;
	
	@JsonProperty
	public BigDecimal change;
	
	@JsonProperty
	public BigDecimal changePercent;
	
	@JsonProperty
	public BigDecimal marketCap;
	
	@JsonProperty
	public BigDecimal peRatio;
	
	@JsonProperty
	public BigDecimal week52High;
	
	@JsonProperty
	public BigDecimal week52Low;
	
	@JsonProperty
	public BigDecimal extendedPrice;
	
	@JsonProperty
	public BigDecimal extendedChange;
	
	@JsonProperty
	public BigDecimal extendedChangePercent;
	
	@JsonProperty
	public Long extendedPriceTime;
	
	@JsonProperty
	public BigDecimal iexRealtimePrice;
	
	@JsonProperty
	public Long iexLastUpdated;
	
	
}

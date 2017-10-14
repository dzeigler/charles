package com.charlesbot.cryptocompare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceRequestResult {

	@JsonProperty("DISPLAY")
	public DisplayProperty display;

	@Override
	public String toString() {
		return "PriceRequestResult [display=" + display + "]";
	}
	
	
}

package com.charlesbot.iex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock {

	@JsonProperty("quote")
	Quote quote;
	
	@JsonProperty("stats")
	Stats stats;
}

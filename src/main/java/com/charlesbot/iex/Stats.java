package com.charlesbot.iex;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

	@JsonProperty
	BigDecimal dividendYield;
	
	@JsonProperty("latestEPS")
	BigDecimal latestEps;
}

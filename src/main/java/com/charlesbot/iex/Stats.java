package com.charlesbot.iex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

	@JsonProperty
	BigDecimal dividendYield;
	
	@JsonProperty("latestEPS")
	BigDecimal latestEps;
}

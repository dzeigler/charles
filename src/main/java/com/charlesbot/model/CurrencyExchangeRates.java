package com.charlesbot.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "data")
public class CurrencyExchangeRates {

	@JsonProperty("currency")
	private String baseCurrency;
	
	@JsonProperty("rates")
	private final Map<String, BigDecimal> rates = new HashMap<>();
	
	public Map<String, BigDecimal> getRates() {
		return rates;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	@Override
	public String toString() {
		return "CurrencyExchangeRates [baseCurrency=" + baseCurrency + ", rates=" + rates + "]";
	}
	
}

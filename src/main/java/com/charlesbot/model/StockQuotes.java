package com.charlesbot.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockQuotes {

	@JsonProperty("quote")
	private final List<StockQuote> quotes = new ArrayList<>();
	
	public List<StockQuote> get() {
		return quotes;
	}

}

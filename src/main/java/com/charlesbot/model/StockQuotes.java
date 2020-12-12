package com.charlesbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class StockQuotes {

	@JsonProperty("quote")
	private final List<StockQuote> quotes = new ArrayList<>();
	
	public List<StockQuote> get() {
		return quotes;
	}

}

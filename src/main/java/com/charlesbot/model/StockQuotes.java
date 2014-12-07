package com.charlesbot.model;

import java.util.ArrayList;
import java.util.List;

public class StockQuotes {

	private final List<StockQuote> quotes = new ArrayList<>();
	
	public List<StockQuote> get() {
		return quotes;
	}

}

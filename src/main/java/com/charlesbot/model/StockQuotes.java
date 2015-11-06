package com.charlesbot.model;

import java.util.ArrayList;
import java.util.List;

public class StockQuotes {

	private final List<StockQuote> quotes = new ArrayList<>();
	
	private String command;
	
	public List<StockQuote> get() {
		return quotes;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}

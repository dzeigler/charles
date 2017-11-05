package com.charlesbot.iex;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchResponse {

	@JsonIgnore
	private final List<Stock> stocks = new ArrayList<>();
	
	@JsonAnySetter
    public void setDynamicProperty(String _ignored, Stock stock) {
        stocks.add(stock);
    }

	public List<Stock> getStocks() {
		return stocks;
	}

	@Override
	public String toString() {
		return "BatchResponse [stocks=" + stocks + "]";
	}

}

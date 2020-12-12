package com.charlesbot.cryptocompare;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplayProperty {

	@JsonIgnore
	private final List<PriceInfo> prices = new ArrayList<>();
	
	@JsonAnySetter
    public void setDynamicProperty(String _ignored, Map<String, PriceInfo> map) {
        PriceInfo currency = map.values().iterator().next();
        currency.fromCode = _ignored;
        currency.toCode = map.keySet().iterator().next();
        prices.add(currency);
    }

	public List<PriceInfo> getPrices() {
		return prices;
	}

	@Override
	public String toString() {
		return "DisplayProperty [prices=" + prices + "]";
	}

}

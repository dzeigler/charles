package com.charlesbot.model;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

public class StockQuotePercentageComparator implements Comparator<StockQuote> {

	@Override
	public int compare(StockQuote o1, StockQuote o2) {
		return ObjectUtils.compare(o1.getTotalChangeInPercent(), o2.getTotalChangeInPercent());
	}

}

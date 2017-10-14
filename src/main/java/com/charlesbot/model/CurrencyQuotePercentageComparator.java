package com.charlesbot.model;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

public class CurrencyQuotePercentageComparator implements Comparator<CurrencyQuote> {

	@Override
	public int compare(CurrencyQuote o1, CurrencyQuote o2) {
		return ObjectUtils.compare(o1.getChangePercent24Hour(), o2.getChangePercent24Hour());
	}

}

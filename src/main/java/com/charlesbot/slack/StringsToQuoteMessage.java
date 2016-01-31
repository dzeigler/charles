package com.charlesbot.slack;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.charlesbot.google.GoogleFinanceClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotePercentageComparator;
import com.charlesbot.model.StockQuotes;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class StringsToQuoteMessage implements Converter<List<String>, QuoteMessage> {

	RangeMap<Double, String> percentRanges;
	private GoogleFinanceClient googleFinanceClient;
	
	public StringsToQuoteMessage() {
		// initialize the percent ranges
		percentRanges = TreeRangeMap.create();
		percentRanges.put(Range.atLeast(10d), "green5");       // [10, +∞)
		percentRanges.put(Range.closedOpen(6d,10d), "green4"); // [6, 10)
		percentRanges.put(Range.closedOpen(3d,6d), "green3");  // [3, 6)
		percentRanges.put(Range.closedOpen(1d,3d), "green2");  // [1, 3)
		percentRanges.put(Range.open(0d,1d), "green1");        // (0, 1)
		percentRanges.put(Range.closed(0d,0d), "black");       // [0, 0]
		percentRanges.put(Range.open(-1d,0d), "red1");         // (-1, 0)
		percentRanges.put(Range.openClosed(-3d,-1d), "red2");  // (-3, -1]
		percentRanges.put(Range.openClosed(-6d,-3d), "red3");  // (-6, -3]
		percentRanges.put(Range.openClosed(-10d,-6d), "red4"); // (-10, -6]
		percentRanges.put(Range.atMost(-10d), "red5");         // (-∞, -10]
	}
	
	public StringsToQuoteMessage(GoogleFinanceClient googleFinanceClient) {
		this();
		this.googleFinanceClient = googleFinanceClient;

	}
	
	@Override
	public QuoteMessage convert(List<String> symbols) {
		StockQuotes stockQuotes = googleFinanceClient.getStockQuotes(symbols).get();
		
		QuoteMessage message = new QuoteMessage();
		
		StringBuilder sb = new StringBuilder();
		if (stockQuotes.get().size() > 1) {
			sb.append(">>>");
		}
		
		// sort the quotes by the percentage change
		stockQuotes.get().sort(new StockQuotePercentageComparator());
		
		for (StockQuote quote : stockQuotes.get()) {
			String colorEmoji = determineRangeString(quote);
			sb.append(MessageFormat.format(":_charles_{5}: {0} ({4}): {1} {2} {3}%", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName(), colorEmoji));
			if (!StringUtils.isEmpty(quote.getExtendedHoursPrice())) {
				sb.append(MessageFormat.format(" extended hours: {0} {1} {2}%", quote.getExtendedHoursPrice(), quote.getExtendedHoursChange(), quote.getExtendedHoursChangeInPercent()));
			}
				
			if (stockQuotes.get().size() > 1) {
				sb.append("\n");
			}
		}
		message.setText(sb.toString());
		
		return message;
	}
	
	String determineRangeString(StockQuote quote) {
		
		double totalChangeInPercent = quote.getTotalChangeInPercent();
		
		String rangeString = percentRanges.get(totalChangeInPercent);
		
		return rangeString;
	}

}

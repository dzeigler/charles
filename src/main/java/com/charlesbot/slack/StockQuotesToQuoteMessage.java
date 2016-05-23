package com.charlesbot.slack;

import java.text.MessageFormat;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotePercentageComparator;
import com.charlesbot.model.StockQuotes;

public class StockQuotesToQuoteMessage implements Converter<StockQuotes, QuoteMessage> {

	@Override
	public QuoteMessage convert(StockQuotes stockQuotes) {
		QuoteMessage message = new QuoteMessage();
		
		StringBuilder sb = new StringBuilder();
		if (stockQuotes.get().size() > 1) {
			sb.append(">>>");
		}
		
		// sort the quotes by the percentage change
		stockQuotes.get().sort(new StockQuotePercentageComparator());
		
		for (StockQuote quote : stockQuotes.get()) {
			String colorEmoji = determineColor(quote);
			sb.append(MessageFormat.format(":{5}: {0} ({4}): {1} {2} {3}%", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName(), colorEmoji));
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
	
	private String determineColor(StockQuote quote) {
		
		double totalChangeInPercent = quote.getTotalChangeInPercent();
		
		String color = "charles_eq0";
		if (totalChangeInPercent <= -10) {
			color = "charles_lte-10";
		} else if (totalChangeInPercent <= -6) {
			color = "charles_lte-6";
		} else if (totalChangeInPercent <= -3) {
			color = "charles_lte-3";
		} else if (totalChangeInPercent <= -1) {
			color = "charles_lte-1";
		} else if (totalChangeInPercent < 0) {
			color = "charles_lt0";
		} else if (totalChangeInPercent == 0) {
			color = "charles_eq0";
		} else if (totalChangeInPercent <= 1) {
			color = "charles_lte1";
		} else if (totalChangeInPercent <= 3) {
			color = "charles_lte3";
		} else if (totalChangeInPercent <= 6) {
			color = "charles_lte6";
		} else if (totalChangeInPercent < 10) {
			color = "charles_lt10";
		} else {
			color = "charles_gte10";
		}
		
		
		return color;
	}

}

package com.charlesbot.slack;

import java.text.MessageFormat;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

public class StockQuotesToQuoteMessage implements Converter<StockQuotes, QuoteMessage> {

	@Override
	public QuoteMessage convert(StockQuotes stockQuotes) {
		QuoteMessage message = new QuoteMessage();
		
		StringBuilder sb = new StringBuilder();
		if (stockQuotes.get().size() > 1) {
			sb.append(">>>");
		}
		for (StockQuote quote : stockQuotes.get()) {
			sb.append(MessageFormat.format("{0} ({4}): {1} {2} {3}%", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName()));
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

}

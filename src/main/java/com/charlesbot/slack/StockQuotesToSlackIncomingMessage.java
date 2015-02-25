package com.charlesbot.slack;

import java.text.MessageFormat;

import org.springframework.core.convert.converter.Converter;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

public class StockQuotesToSlackIncomingMessage implements Converter<StockQuotes, SlackIncomingMessage> {

	@Override
	public SlackIncomingMessage convert(StockQuotes stockQuotes) {
		SlackIncomingMessage message = new SlackIncomingMessage();
		
		StringBuilder sb = new StringBuilder();
		if (stockQuotes.get().size() > 1) {
			sb.append(">>>");
		}
		for (StockQuote quote : stockQuotes.get()) {
			sb.append(MessageFormat.format("{0} ({4}): {1} {2} {3}", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName()));
			if (stockQuotes.get().size() > 1) {
				sb.append("\n");
			}
		}
		message.setText(sb.toString());
		
		return message;
	}

}

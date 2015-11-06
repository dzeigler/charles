package com.charlesbot.slack;

import java.text.MessageFormat;

import org.springframework.core.convert.converter.Converter;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

public class StockQuotesToStatsMessage implements Converter<StockQuotes, StatsMessage> {

	@Override
	public StatsMessage convert(StockQuotes stockQuotes) {
		StatsMessage message = new StatsMessage();
		
		StringBuilder sb = new StringBuilder();
		if (stockQuotes.get().size() > 1) {
			sb.append(">>>");
		}
		for (StockQuote quote : stockQuotes.get()) {
			sb.append(MessageFormat.format("{0} ({1}): {2}, p/e {3}, eps {4}, day range {5}/{6}, 52 week range {7}/{8}, div {9}, yield {10}, shares {11}, beta {12}"
					, quote.getSymbol(), quote.getName(), quote.getMarketCap(), quote.getPe(), quote.getEps(), quote.getDayLow(), quote.getDayHigh(),
					quote.getFiftyTwoWeekLow(), quote.getFiftyTwoWeekHigh(), quote.getDividend(), quote.getYield(), quote.getShares(), quote.getBeta()));
			
			if (stockQuotes.get().size() > 1) {
				sb.append("\n");
			}
		}
		message.setText(sb.toString());
		
		return message;
	}

}

package com.charlesbot.slack;

import java.text.MessageFormat;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

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
			switch (stockQuotes.getCommand()) {
			case SlackRestController.QUOTE_COMMAND:
				if (StringUtils.isEmpty(quote.getExtendedHoursPrice())) {
					sb.append(MessageFormat.format("{0} ({4}): {1} {2} {3}%", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName()));
				} else {
					sb.append(MessageFormat.format("{0} ({4}): {1} {2} {3}% extended hours: {5} {6} {7}%", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName()
							,quote.getExtendedHoursPrice(), quote.getExtendedHoursChange(), quote.getExtendedHoursChangeInPercent()));
				}
				break;
			case SlackRestController.STATS_COMMAND:
				sb.append(MessageFormat.format("{0} ({1}): {2}, p/e {3}, eps {4}, day range {5}/{6}, 52 week range {7}/{8}, div {9}, yield {10}, shares {11}, beta {12}"
						, quote.getSymbol(), quote.getName(), quote.getMarketCap(), quote.getPe(), quote.getEps(), quote.getDayLow(), quote.getDayHigh(),
						quote.getLowFiftyTwoWeek(), quote.getHighFiftyTwoWeek(), quote.getDividend(), quote.getYield(), quote.getShares(), quote.getBeta()));
				break;
			}
			
			if (stockQuotes.get().size() > 1) {
				sb.append("\n");
			}
		}
		message.setText(sb.toString());
		
		return message;
	}

}

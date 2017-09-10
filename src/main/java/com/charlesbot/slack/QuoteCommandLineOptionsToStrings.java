package com.charlesbot.slack;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.QuoteCommandLineOptions;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotePercentageComparator;
import com.charlesbot.model.StockQuotes;
import com.charlesbot.yahoo.YahooFinanceClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class QuoteCommandLineOptionsToStrings implements CommandConverter<QuoteCommandLineOptions> {

	RangeMap<Double, String> percentRanges;
	private YahooFinanceClient YahooFinanceClient;
	
	public QuoteCommandLineOptionsToStrings() {
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
	
	public QuoteCommandLineOptionsToStrings(YahooFinanceClient YahooFinanceClient) {
		this();
		this.YahooFinanceClient = YahooFinanceClient;
	}
	
	@Override
	public List<String> convert(QuoteCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append("```"+helpMessage+"```");
		} else {
			Optional<StockQuotes> stockQuotesResult = YahooFinanceClient.getStockQuotes(options.tickerSymbols);
			if (stockQuotesResult.isPresent()) {
				StockQuotes stockQuotes = stockQuotesResult.get();
			
				if (stockQuotes.get().size() > 1) {
					output.append(">>>");
				}
				
				// sort the quotes by the percentage change
				stockQuotes.get().sort(new StockQuotePercentageComparator());
				
				for (StockQuote quote : stockQuotes.get()) {
					String colorEmoji = determineRangeString(quote);
					output.append(MessageFormat.format(":_charles_{5}: {0} ({4}): {1} {2} {3}%", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName(), colorEmoji));
					if (!StringUtils.isEmpty(quote.getExtendedHoursPrice())) {
						output.append(MessageFormat.format(" extended hours: {0} {1} {2}%", quote.getExtendedHoursPrice(), quote.getExtendedHoursChange(), quote.getExtendedHoursChangeInPercent()));
					}
						
					if (stockQuotes.get().size() > 1) {
						output.append("\n");
					}
				}
			} else {
				output.append("Yahoo finance can't find a quote for the symbol you provided.");
			}
		}
		return Lists.newArrayList(output.toString());
	}
	
	String determineRangeString(StockQuote quote) {
		
		double totalChangeInPercent = quote.getTotalChangeInPercent();
		
		String rangeString = percentRanges.get(totalChangeInPercent);
		
		return rangeString;
	}

}

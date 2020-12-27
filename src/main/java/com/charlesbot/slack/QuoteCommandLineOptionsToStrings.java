package com.charlesbot.slack;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.QuoteCommandLineOptions;
import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotePercentageComparator;
import com.charlesbot.model.StockQuotes;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class QuoteCommandLineOptionsToStrings implements CommandConverter<QuoteCommandLineOptions> {

	private PercentRanges percentRanges;

	private IexStockQuoteClient iexStockQuoteClient;
	
	public QuoteCommandLineOptionsToStrings() {
		
	}
	
	public QuoteCommandLineOptionsToStrings(IexStockQuoteClient iexStockQuoteClient, PercentRanges percentRanges) {
		this();
		this.iexStockQuoteClient = iexStockQuoteClient;
		this.percentRanges = percentRanges;
	}
	
	@Override
	public List<String> convert(QuoteCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append("```"+helpMessage+"```");
		} else {
			Optional<StockQuotes> stockQuotesResult = iexStockQuoteClient.getStockQuotes(options.tickerSymbols);
			if (stockQuotesResult.isPresent()) {
				StockQuotes stockQuotes = stockQuotesResult.get();
			
				if (stockQuotes.get().size() > 1) {
					output.append(">>>");
				}
				
				// sort the quotes by the percentage change
				stockQuotes.get().sort(new StockQuotePercentageComparator());
				
				for (StockQuote quote : stockQuotes.get()) {
					String colorEmoji = determineRangeString(quote);
					output.append(MessageFormat.format(":_charles_{5}: {0} ({4}): ${1} [${2} {3}%]", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName(), colorEmoji));
					if (!ObjectUtils.isEmpty(quote.getExtendedHoursPrice())) {
						output.append(MessageFormat.format(" extended hours: {0} {1} {2}%", quote.getExtendedHoursPrice(), quote.getExtendedHoursChange(), quote.getExtendedHoursChangeInPercent()));
					}
						
					if (stockQuotes.get().size() > 1) {
						output.append("\n");
					}
				}
			} else {
				output.append("IEX can't find a quote for the symbol you provided.");
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

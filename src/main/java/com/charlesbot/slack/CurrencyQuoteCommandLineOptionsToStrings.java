package com.charlesbot.slack;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.CurrencyQuoteCommandLineOptions;
import com.charlesbot.cryptocompare.CryptoCompareClient;
import com.charlesbot.model.CurrencyQuote;
import com.charlesbot.model.CurrencyQuotePercentageComparator;
import com.google.common.collect.Lists;

public class CurrencyQuoteCommandLineOptionsToStrings implements CommandConverter<CurrencyQuoteCommandLineOptions> {

	private static final Logger log = LoggerFactory.getLogger(CurrencyQuoteCommandLineOptionsToStrings.class);
	
	private PercentRanges percentRanges;
	private CryptoCompareClient cryptoCompareClient;
	
	public CurrencyQuoteCommandLineOptionsToStrings() {
	}
	
	public CurrencyQuoteCommandLineOptionsToStrings(CryptoCompareClient cryptoCompareClient, PercentRanges percentRanges) {
		this();
		this.cryptoCompareClient = cryptoCompareClient;
		this.percentRanges = percentRanges;
	}
	
	@Override
	public List<String> convert(CurrencyQuoteCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append("```"+helpMessage+"```");
		} else {
			List<CurrencyQuote> prices = cryptoCompareClient.getPrices(options.fromCurrency, options.toCurrency);
			
			// sort the quotes by the percentage change
			prices.sort(new CurrencyQuotePercentageComparator());
			
			if (prices.size() > 1) {
				output.append(">>>");
			}
			for (CurrencyQuote price : prices) {
				
				output.append(MessageFormat.format(":_charles_{0}: {1} 1 = {2} [{3} {4}%]"
						, determineRangeString(price)
						, price.getFromSymbol()
						, price.getPrice()
						, price.getChange24Hour()
						, price.getChangePercent24Hour()
						));
				
				if (prices.size() > 1) {
					output.append("\n");
				}
						
			}
			if (prices.isEmpty()) {
				output.append("CryptoCompare can't find prices for " + options.getFormattedCurrencyPair() + ".");
			}
		}
		
		return Lists.newArrayList(output.toString());
	}
	
	String determineRangeString(CurrencyQuote price) {
		
		try {
			double totalChangeInPercent = price.getTotalChangeInPercent();
			
			String rangeString = percentRanges.get(totalChangeInPercent);
		
			return rangeString;
		} catch (Exception e) {
			log.warn("Wasn't able to parse the percent change for {}", price);
			log.warn("Exception: ", e);
			return percentRanges.get(0);
		}
	}
	
}

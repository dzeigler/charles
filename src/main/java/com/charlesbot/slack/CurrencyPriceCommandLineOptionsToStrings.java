package com.charlesbot.slack;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.CurrencyPriceCommandLineOptions;
import com.charlesbot.cryptocompare.CryptoCompareClient;
import com.charlesbot.model.CurrencyPrice;
import com.google.common.collect.Lists;

public class CurrencyPriceCommandLineOptionsToStrings implements CommandConverter<CurrencyPriceCommandLineOptions> {

	private static final Logger log = LoggerFactory.getLogger(CurrencyPriceCommandLineOptionsToStrings.class);
	
	private PercentRanges percentRanges;
	private CryptoCompareClient cryptoCompareClient;
	
	public CurrencyPriceCommandLineOptionsToStrings() {
	}
	
	public CurrencyPriceCommandLineOptionsToStrings(CryptoCompareClient cryptoCompareClient, PercentRanges percentRanges) {
		this();
		this.cryptoCompareClient = cryptoCompareClient;
		this.percentRanges = percentRanges;
	}
	
	@Override
	public List<String> convert(CurrencyPriceCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append("```"+helpMessage+"```");
		} else {
			List<CurrencyPrice> prices = cryptoCompareClient.getPrices(options.fromCurrency, options.toCurrency);
			if (prices.size() > 1) {
				output.append(">>>");
			}
			for (CurrencyPrice price : prices) {
				
				output.append(MessageFormat.format(":_charles_{0}: {1}: {2} 1 = {3} [{4} {5}%]"
						, determineRangeString(price)
						, price.getFromCurrency()
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
	
	String determineRangeString(CurrencyPrice price) {
		
		try {
			double totalChangeInPercent = Double.parseDouble(price.getChangePercent24Hour());
			
			String rangeString = percentRanges.get(totalChangeInPercent);
		
			return rangeString;
		} catch (Exception e) {
			log.warn("Wasn't able to parse the percent change for {}", price);
			log.warn("Exception: ", e);
			return percentRanges.get(0);
		}
	}
	
}

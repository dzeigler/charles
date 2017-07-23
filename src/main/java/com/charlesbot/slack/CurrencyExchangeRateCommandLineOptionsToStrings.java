package com.charlesbot.slack;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.CurrencyExchangeRateCommandLineOptions;
import com.charlesbot.coinbase.CoinBaseClient;
import com.charlesbot.model.CurrencyExchangeRates;
import com.google.common.collect.Lists;
import com.google.common.collect.RangeMap;

public class CurrencyExchangeRateCommandLineOptionsToStrings implements CommandConverter<CurrencyExchangeRateCommandLineOptions> {

	RangeMap<Double, String> percentRanges;
	private CoinBaseClient coinBaseClient;
	
	public CurrencyExchangeRateCommandLineOptionsToStrings() {
	}
	
	public CurrencyExchangeRateCommandLineOptionsToStrings(CoinBaseClient coinBaseClient) {
		this();
		this.coinBaseClient = coinBaseClient;
	}
	
	@Override
	public List<String> convert(CurrencyExchangeRateCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append("```"+helpMessage+"```");
		} else {
			Optional<CurrencyExchangeRates> exchangeRates = coinBaseClient.getExchangeRates(options.quoteCurrency);
			if (exchangeRates.isPresent()) {
				CurrencyExchangeRates currencyExchangeRates = exchangeRates.get();
			
				BigDecimal rate = currencyExchangeRates.getRates().get(options.baseCurrency);
				
				output.append(MessageFormat.format("{0} {1} = {2,number,#.########} {3}", 1, options.quoteCurrency, rate, options.baseCurrency));
						
			} else {
				output.append("Coinbase can't find the exchnage rate for the currency pair " + options.getFormattedCurrencyPair() + ".");
			}
		}
		
		return Lists.newArrayList(output.toString());
	}
	
}

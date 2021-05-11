package com.charlesbot.slack;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.CurrencyQuoteCommandLineOptions;
import com.charlesbot.cryptocompare.CryptoCompareClient;
import com.charlesbot.model.CurrencyQuote;
import com.charlesbot.model.CurrencyQuotePercentageComparator;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class CurrencyQuoteCommandLineOptionsToStrings implements CommandConverter<CurrencyQuoteCommandLineOptions> {

	private static final Logger log = LoggerFactory.getLogger(CurrencyQuoteCommandLineOptionsToStrings.class);

	private final WatchListRepository watchListRepository;
	private final CryptoCompareClient cryptoCompareClient;
	private final PercentRanges percentRanges;

	@Override
	public List<String> convert(CurrencyQuoteCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append("```"+helpMessage+"```");
		} else {
			if (options.fromCurrency.isEmpty()) {
				String userId = options.getSenderUserId();
				WatchList watchList = watchListRepository.findByUserIdAndName(userId, "cq");
				if (watchList == null) {
					options.fromCurrency = Arrays.asList("BTC", "ADA", "XMR", "ETH", "DOT", "LTC", "XLM");
				} else {
					options.fromCurrency = watchList.transactions.stream()
							.filter(t -> StringUtils.isNotBlank(t.getSymbol()))
							.map(t -> t.getSymbol())
							.collect(Collectors.toList());
				}
			}

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

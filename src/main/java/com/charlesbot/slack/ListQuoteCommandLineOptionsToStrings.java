package com.charlesbot.slack;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListQuoteCommandLineOptions;
import com.charlesbot.cli.QuoteCommandLineOptions;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;

public class ListQuoteCommandLineOptionsToStrings implements CommandConverter<ListQuoteCommandLineOptions> {

	private WatchListRepository watchListRepository;
	private QuoteCommandLineOptionsToStrings quoteCommandLineOptionsToString;

	public ListQuoteCommandLineOptionsToStrings(WatchListRepository watchListRepository, QuoteCommandLineOptionsToStrings quoteCommandLineOptionsToString) {
		this.watchListRepository = watchListRepository;
		this.quoteCommandLineOptionsToString = quoteCommandLineOptionsToString;
	}

	@Override
	public List<String> convert(ListQuoteCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			output.append("```");
			for (String error: options.getErrors()) {
				output.append(error + "\n");
			}
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append(helpMessage + "```");
		} else if (!options.getWarnings().isEmpty()) {
			for (String warning : options.getWarnings()) { 
				output.append(warning + "\n");
			}
		} else {
			
			WatchList watchList = watchListRepository.findByUserIdAndName(options.userId, options.watchListName);
		
			if (watchList == null) {
				output.append("No list named " + options.watchListName + " for that user.");
			} else {
				List<String> symbols = watchList.transactions.stream()
			        .filter(tx -> tx != null && tx.getSymbol() != null)
			        .map(tx -> tx.getSymbol().toUpperCase())
			        .distinct()
			        .collect(Collectors.toList());
				if (symbols.isEmpty()) {
					output.append("This list is empty");
				} else {
					// need to execute the quotes
					QuoteCommandLineOptions qCmd = new QuoteCommandLineOptions();
					qCmd.tickerSymbols = symbols;
					List<String> replies = quoteCommandLineOptionsToString.convert(qCmd);
					for (String reply : replies) {
						output.append(reply);
					}
				}
			}
		}
		return Lists.newArrayList(output.toString());
	}

}

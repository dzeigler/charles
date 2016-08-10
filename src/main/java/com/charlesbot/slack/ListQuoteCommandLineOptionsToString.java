package com.charlesbot.slack;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListQuoteCommandLineOptions;
import com.charlesbot.cli.QuoteCommandLineOptions;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;

@Component
public class ListQuoteCommandLineOptionsToString implements Converter<ListQuoteCommandLineOptions, String> {

	@Autowired
	private WatchListRepository watchListRepository;
	
	@Autowired
	private QuoteCommandLineOptionsToString quoteCommandLineOptionsToString;

	public ListQuoteCommandLineOptionsToString() {
	}

	public ListQuoteCommandLineOptionsToString(WatchListRepository watchListRepository) {
		this.watchListRepository = watchListRepository;
	}

	@Override
	public String convert(ListQuoteCommandLineOptions options) {
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
				output.append("No list named " + options.watchListName + " for you.");
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
					String reply = quoteCommandLineOptionsToString.convert(qCmd);
					output.append(reply);
				}
			}
		}
		return output.toString();
	}

}

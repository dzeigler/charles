package com.charlesbot.slack;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListQuoteCommandLineOptions;
import com.charlesbot.cli.QuoteCommandLineOptions;
import com.charlesbot.model.Position;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.charlesbot.service.PositionService;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListQuoteCommandLineOptionsToStrings implements CommandConverter<ListQuoteCommandLineOptions> {

	private final WatchListRepository watchListRepository;
	private final QuoteCommandLineOptionsToStrings quoteCommandLineOptionsToString;
	private final PositionService positionService;

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
				Collection<Position> positionsList = positionService.getPositionsList(watchList);
				boolean hasPrices = positionsList.stream().anyMatch(p -> p.getPrice().compareTo(BigDecimal.ZERO) != 0 && p.getQuantity().compareTo(BigDecimal.ZERO) != 0);
				List<String> symbols = new ArrayList<>();
				if (hasPrices) {
					symbols = positionsList.stream()
							.filter(position -> position != null && position.getSymbol() != null)
							.filter(position -> position.getPrice() != null && position.getQuantity().compareTo(BigDecimal.ZERO) != 0)
							.map(position -> position.getSymbol().toUpperCase())
							.distinct()
							.collect(Collectors.toList());
				} else {
					symbols = positionsList.stream()
							.filter(position -> position != null && position.getSymbol() != null)
							.map(position -> position.getSymbol().toUpperCase())
							.distinct()
							.collect(Collectors.toList());
				}
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

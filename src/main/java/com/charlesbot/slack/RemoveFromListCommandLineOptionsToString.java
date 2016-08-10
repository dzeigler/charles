package com.charlesbot.slack;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.RemoveFromListCommandLineOptions;
import com.charlesbot.model.Transaction;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;

@Component
public class RemoveFromListCommandLineOptionsToString implements Converter<RemoveFromListCommandLineOptions, String> {

	@Autowired
	private WatchListRepository watchListRepository;

	public RemoveFromListCommandLineOptionsToString() {
	}

	public RemoveFromListCommandLineOptionsToString(WatchListRepository watchListRepository) {
		this.watchListRepository = watchListRepository;
	}

	@Override
	public String convert(RemoveFromListCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			output.append("```");
			for (String error: options.getErrors()) {
				output.append(error + "\n");
			}
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append(helpMessage + "```");
		} if (!options.getWarnings().isEmpty()) {
			for (String warning : options.getWarnings()) { 
				output.append(warning + "\n");
			}
		} else {
			WatchList watchList = watchListRepository.findByUserIdAndName(options.userId, options.watchListName);
			if (watchList == null) {
				output.append("No list named " + options.watchListName + " found for you");
			} else {
				if (options.deleteList) {
					watchListRepository.delete(watchList);
					output.append(watchList.name + " has been deleted");
				} else {
					List<String> upperCaseSymbols = options.tickerSymbols.stream()
						.map(String::toUpperCase)
						.collect(Collectors.toList())
						;
				
					Optional<Transaction> transactionToRemove = watchList.transactions.stream()
						.filter(t -> upperCaseSymbols.contains(t.getSymbol().toUpperCase()))
						.findFirst();
					
					if (transactionToRemove.isPresent()) {
						watchList.transactions.remove(transactionToRemove.get());
						if (watchList.transactions.isEmpty()) {
							watchListRepository.delete(watchList);
							output.append("Removing " + transactionToRemove.get().getSymbol() + " and deleting the empty list " + watchList.name);
						} else {
							watchListRepository.save(watchList);
							output.append("Removing " + transactionToRemove.get().getSymbol() + " from list " + watchList.name);
						}
						
					} else {
						output.append("I couldn't find the symbol " + options.tickerSymbols + " in " + watchList.name + "."); 
					}
				}
			}
		}
		return output.toString();
	}

}

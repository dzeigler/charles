package com.charlesbot.slack;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListCommandLineOptions;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;

@Component
public class ListCommandLineOptionsToString implements Converter<ListCommandLineOptions, String> {

	@Autowired
	private WatchListRepository watchListRepository;

	public ListCommandLineOptionsToString() {
	}

	public ListCommandLineOptionsToString(WatchListRepository watchListRepository) {
		this.watchListRepository = watchListRepository;
	}

	@Override
	public String convert(ListCommandLineOptions options) {
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
			
			if (options.watchListName == null) {
				List<WatchList> watchLists = watchListRepository.findByUserId(options.userId);
				String reply = watchLists.stream()
			        .map(wl -> wl.name)
			        .collect(Collectors.joining("\t"));
				output.append(reply);
			} else {
				WatchList watchList = watchListRepository.findByUserIdAndName(options.userId, options.watchListName);
			
				if (watchList == null) {
					output.append("No list named " + options.watchListName + " for you.");
				} else {
					String reply = watchList.transactions.stream()
				        .map(tx -> "[" + watchList.transactions.indexOf(tx) + "] - " + tx.toString(","))
				        .collect(Collectors.joining("\n"));
					output.append(reply);
				}
			}
		}
		return output.toString();
	}

}
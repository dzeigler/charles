package com.charlesbot.slack;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.charlesbot.cli.AddToListCommandLineOptions;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.model.Transaction;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.RangeMap;

@Component
public class AddToListCommandLineOptionsToString implements Converter<AddToListCommandLineOptions, String> {

	@Autowired
	private WatchListRepository watchListRepository;

	public AddToListCommandLineOptionsToString() {
	}

	public AddToListCommandLineOptionsToString(WatchListRepository watchListRepository) {
		this.watchListRepository = watchListRepository;
	}

	@Override
	public String convert(AddToListCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			output.append("```");
			for (String error: options.getErrors()) {
				output.append(error + "\n");
			}
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append(helpMessage + "```");
		} else {
			WatchList watchList = watchListRepository.findByUserIdAndName(options.userId, options.watchListName);
			if (watchList == null) {
				watchList = new WatchList();
				watchList.name = options.watchListName;
				watchList.transactions = new ArrayList<>();
				watchList.userId = options.userId;
			}
			watchList.transactions.addAll(options.transactions);
			watchListRepository.save(watchList);
			for (Transaction t : options.transactions) {
				output.append("Added " + t.getSymbol() + " to list " + watchList.name + "\n");
			}
		}
		return output.toString();
	}

}

package com.charlesbot.slack;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListCommandLineOptions;
import com.charlesbot.model.User;
import com.charlesbot.model.UserRepository;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListCommandLineOptionsToStrings implements CommandConverter<ListCommandLineOptions> {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ListCommandLineOptionsToStrings.class);
	
	private WatchListRepository watchListRepository;
	private UserRepository userRepository;

	public ListCommandLineOptionsToStrings() {
	}

	public ListCommandLineOptionsToStrings(WatchListRepository watchListRepository, UserRepository userRepository) {
		this.watchListRepository = watchListRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<String> convert(ListCommandLineOptions options) {
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
				Optional<User> user = userRepository.findById(options.userId);
				String reply = watchLists.stream()
			        .map(wl -> {
						if (user.isPresent() && wl.equals(user.get().defaultWatchList)) {
							return wl.name + "*";
						}
						return wl.name;
					})
			        .collect(Collectors.joining(" "));
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
		
		return Lists.newArrayList(output.toString());
	}

}

package com.charlesbot.cli;

import com.charlesbot.model.User;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ListStatsCommandLineOptions extends Command {

	public static final String COMMAND = "stats";
	public static final String COMMAND_SYNTAX = COMMAND + " <LIST_NAME>";
	public static final String COMMAND_HEADER = 
			"LIST_NAME is the name of the portfolio to get stats for";
	public static final String COMMAND_DESCRIPTION = "Returns performance stats for the positions in the given list. Only support USD.";
	public static final String COMMAND_PATTERN = "(?i)^@\\w+:?\\p{Z}*stats\\p{Z}*.*";

	static Options options;

	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
		// user
		options.addOption( 
			Option.builder("u")
				.required(false)
				.longOpt("user")
				.hasArg(true)
				.argName("USER_MENTION")
				.desc("mention the user that owns LIST_NAME (e.g. -u @username)")
				.type(String.class)
				.valueSeparator()
				.build()
		);
		// shortened
		options.addOption( 
			Option.builder("s")
				.required(false)
				.longOpt("short")
				.hasArg(false)
				.desc("shows only the header and totals row")
				.type(String.class)
				.valueSeparator()
				.build()
		);
		// order by list percent
		options.addOption(
			Option.builder("l")
				.required(false)
				.longOpt("orders by list percent")
				.hasArg(false)
				.desc("orders the results by list %")
				.type(String.class)
				.valueSeparator()
				.build()
		);
	}

	public String watchListName;
	public String userId;
	public User targetUser;
	public boolean shortened;
	public boolean orderByListPercent;
	
	@Override
	public String getName() {
		return COMMAND;
	}

	@Override
	public String getDescription() {
		return COMMAND_DESCRIPTION;
	}

	@Override
	public String getDescriptionHeader() {
		return COMMAND_HEADER;
	}

	@Override
	public String getSyntax() {
		return COMMAND_SYNTAX;
	}

	@Override
	public Options getOptions() {
		return options;
	}

	@Override
	public void populateOptions(CommandLine commandLine) {
		if (commandLine.hasOption("?")) {
			forceHelp();
		} else if (commandLine.getArgList().isEmpty()) {
			forceHelp();
		} else if (commandLine.getArgList().size() != 1 && commandLine.getArgList().size() != 2) {
			forceHelp();
		}

		String userMention = commandLine.getOptionValue('u');
		if (userMention != null) {
			if (!userMention.contains("@")) {
				forceHelp();
			}
			this.userId = userMention.replaceAll("[<@>]", "");
		} else {
			this.userId = getSenderUserId();
		}

		if (commandLine.getArgList().size() == 1) {
			watchListName = getUserRepository().findById(userId)
					.filter(u -> u.defaultWatchList != null)
					.map(u -> u.defaultWatchList.name)
					.orElse(null);
		} else if (commandLine.getArgList().size() == 2) {
			watchListName = commandLine.getArgList().get(1);
		}

		if (commandLine.hasOption("s")) {
			shortened = true;
		} else if (commandLine.hasOption("l")) {
			orderByListPercent = true;
		}
		
	}
	
	public static boolean matcher(String t) {
		return t.toLowerCase().matches(COMMAND_PATTERN);
	}

	@Override
	public boolean isStandalone() {
		return false;
	}

}

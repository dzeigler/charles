package com.charlesbot.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ListCommandLineOptions extends Command {

	public static final String COMMAND = "ls";
	public static final String COMMAND_SYNTAX = COMMAND + " [<LIST_NAME>]";
	public static final String COMMAND_HEADER = 
			"LIST_NAME is the name of the watch list to look up. If provided, all items on the list will be returned.";
	public static final String COMMAND_DESCRIPTION = "Lists all the lists for a user or all of the items on a watch list";
	public static final String COMMAND_PATTERN = "(?i)^@\\w+:?\\p{Z}*ls.*";

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
					.desc("mention the user whose lists should be returned (e.g. -u @username)")
					.type(String.class)
					.valueSeparator()
					.build()
			);

	}
	
	public String watchListName;
	public String userId;
	
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
	public void populateOptions(CommandLine commandLine, String senderUserId) {
		if (commandLine.hasOption("?")) {
			forceHelp();
		} else if (commandLine.getArgList().isEmpty()) {
			forceHelp();
		} else if (commandLine.getArgList().size() != 1 && commandLine.getArgList().size() != 2) {
			forceHelp();
		}

		if (commandLine.getArgList().size() == 2) {
			watchListName = commandLine.getArgList().get(1);
		}
		
		String userMention = commandLine.getOptionValue('u');
		if (userMention != null) {
			if (!userMention.contains("@")) {
				forceHelp();
			}
			this.userId = userMention.replaceAll("[<@>]", "");
		} else {
			this.userId = senderUserId;
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

package com.charlesbot.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class ListQuoteCommandLineOptions extends Command {

	public static final String COMMAND = "q";
	public static final String COMMAND_SYNTAX = COMMAND + " <LIST_NAME>";
	public static final String COMMAND_HEADER = 
			"LIST_NAME is the name watch list to get quotes for";
	public static final String COMMAND_DESCRIPTION = "Returns quotes for the ticker symbol(s) in the given list";
	public static final String COMMAND_PATTERN = "^@\\w+:?\\s*q.*";

	static Options options;

	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");

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
	public void populateOptions(CommandLine commandLine, String userId) {
		if (commandLine.getArgList().isEmpty()) {
			forceHelp();
		} else if (commandLine.getArgList().size() != 2) {
			forceHelp();
		}

		this.userId = userId;
		if (commandLine.getArgList().size() == 2) {
			watchListName = commandLine.getArgList().get(1);
		}
	}
	
	public static boolean matcher(String t) {
		return t.matches(COMMAND_PATTERN);
	}

	@Override
	public boolean isStandalone() {
		return false;
	}

}

package com.charlesbot.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class ListStatsCommandLineOptions extends Command {

	public static final String COMMAND = "stats";
	public static final String COMMAND_SYNTAX = COMMAND + " <LIST_NAME>";
	public static final String COMMAND_HEADER = 
			"LIST_NAME is the name of the portfolio to get stats for";
	public static final String COMMAND_DESCRIPTION = "Returns performance stats for the positions in the given list";
	public static final String COMMAND_PATTERN = "^@\\w+:?\\s*stats.*";

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
		// TODO Auto-generated method stub
		return false;
	}

}

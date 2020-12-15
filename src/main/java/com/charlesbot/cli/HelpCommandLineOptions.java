package com.charlesbot.cli;

import com.charlesbot.model.User;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class HelpCommandLineOptions extends Command {

	public static final String COMMAND = "help";
	public static final String COMMAND_DESCRIPTION = "Prints this message";
	public static final String COMMAND_SYNTAX = COMMAND;
	public static final String COMMAND_PATTERN = "(?i)^@\\w+:?\\p{Z}+.*";

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
		return null;
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
	public void populateOptions(CommandLine commandLine, User user) {
		forceHelp();
	}
	
	public static boolean matcher(String t) {
		return t.toLowerCase().matches(COMMAND_PATTERN);
	}

	@Override
	public boolean isStandalone() {
		return false;
	}

}

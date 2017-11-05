package com.charlesbot.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class RemoveFromListCommandLineOptions extends Command {

	public static final String COMMAND = "rm";
	public static final String COMMAND_SYNTAX = COMMAND + " <LIST_NAME> [<SYMBOL>]";
	public static final String COMMAND_HEADER = 
			"LIST_NAME is the name watch list being modified\n"
			+ "SYMBOL is the IEX ticker for the stock or index";
	public static final String COMMAND_DESCRIPTION = "Removes the first matching symbol from the list or the entire list if no symbols are provided";
	public static final String COMMAND_PATTERN = "^@\\w+:?\\s*(rm|del).*";

	static Options options;

	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
		options.addOption( 
			Option.builder("f")
				.required(false)
				.longOpt("force")
				.hasArg(false)
				.desc("forces a list to be removed")
				.build()
		);

	}
	
	public String watchListName;
	public List<String> tickerSymbols = new ArrayList<>();
	public String userId;
	public boolean deleteList;
	
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
		if (commandLine.getArgList().isEmpty() || commandLine.hasOption('?')) {
			forceHelp();
		} else if (commandLine.getArgList().size() < 2) {
			forceHelp();
		}

		this.userId = userId;
		watchListName = commandLine.getArgList().get(1);
		List<String> subList = commandLine.getArgList().subList(2, commandLine.getArgList().size());
		this.tickerSymbols.addAll(subList);
		
		if (tickerSymbols.isEmpty()) {
			if (commandLine.hasOption('f')) {
				this.deleteList = true;
			} else {
				addWarning("Use the -f option if you want to delete the entire list");
			}
		}
		
	}
	
	public static boolean matcher(String t) {
		return t.toLowerCase().matches(COMMAND_PATTERN.toLowerCase());
	}

	@Override
	public boolean isStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

}

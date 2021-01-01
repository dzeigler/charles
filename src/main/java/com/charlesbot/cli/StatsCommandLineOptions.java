package com.charlesbot.cli;

import com.charlesbot.model.User;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class StatsCommandLineOptions extends Command {

	public static final String COMMAND = "!stats";
	public static final String COMMAND_SYNTAX = COMMAND + " <SYMBOL>[ <SYMBOL>]...";
	public static final String COMMAND_HEADER = "SYMBOL is the IEX ticker for the stock or index";
	public static final String COMMAND_DESCRIPTION = "Returns information about a stock";
	
	static Options options;
	
	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
	}
	
	public List<String> tickerSymbols = new ArrayList<>();

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
		List<String> argList = commandLine.getArgList();
		if (argList.isEmpty() || commandLine.hasOption('?')) {
			forceHelp();
		} else {
			tickerSymbols.addAll(argList);
		}
	}
	
	public static boolean matcher(String t) {
		return t.toLowerCase().startsWith(COMMAND.toLowerCase());
	}

	@Override
	public boolean isStandalone() {
		return true;
	}
}

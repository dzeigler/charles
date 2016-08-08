package com.charlesbot.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class QuoteCommandLineOptions extends Command {

	public static final String COMMAND = "!q";
	public static final String COMMAND_HEADER = "SYMBOL is the Google Finance ticker for the stock or index";
	public static final String COMMAND_DESCRIPTION = "Returns quotes for the given ticker symbol(s)";
	
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
		return COMMAND+" <SYMBOL>[ <SYMBOL>]...";
	}

	@Override
	public Options getOptions() {
		return options;
	}

	@Override
	public void populateOptions(CommandLine commandLine, String senderUserId) {
		List<String> argList = commandLine.getArgList();
		if (argList.isEmpty() || commandLine.hasOption('?')) {
			forceHelp();
		} else {
			tickerSymbols.addAll(argList);
		}
	}

	public static boolean matcher(String t) {
		return t.startsWith(COMMAND);
	}

	@Override
	public boolean isStandalone() {
		return true;
	}
}

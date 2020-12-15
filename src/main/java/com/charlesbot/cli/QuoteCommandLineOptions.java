package com.charlesbot.cli;

import com.charlesbot.model.User;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class QuoteCommandLineOptions extends Command {

	public static final String COMMAND = "!q";
	public static final String COMMAND_HEADER = "SYMBOL is the IEX ticker for the stock or index";
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
	public void populateOptions(CommandLine commandLine, User user) {
		List<String> argList = commandLine.getArgList();
		if (argList.isEmpty() || commandLine.hasOption('?')) {
			forceHelp();
		} else {
			tickerSymbols.addAll(new ArrayList<>(new LinkedHashSet<>(argList)));
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

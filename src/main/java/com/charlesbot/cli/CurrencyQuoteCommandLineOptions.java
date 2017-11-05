package com.charlesbot.cli;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CurrencyQuoteCommandLineOptions extends Command {

	public static final String DEFAULT_TO_CURRENCY = "USD";
	public static final String COMMAND = "!cq";
	public static final String COMMAND_SYNTAX = COMMAND + " [<FROM_CURRENCY>]...";
	public static final String COMMAND_HEADER = "FROM_CURRENCY is the code for the currency to look up the price for (e.g. BTC, ETH, etc.). Defaults to BTC BTG BCH ZEC XMR ETH";
	public static final String COMMAND_DESCRIPTION = "Returns the price for one unit of the from currency in the to currency.";
	
	static Options options;
	
	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
				
		options.addOption( 
			Option.builder("t")
				.required(false)
				.longOpt("to")
				.hasArgs()
				.numberOfArgs(1)
				.argName("TO_CURRENCY")
				.desc("is the code for the currency the price should be in. Defaults to " + DEFAULT_TO_CURRENCY + ".")
				.type(String.class)
				.build()
		);

	}

	public String toCurrency;
	public List<String> fromCurrency;

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
		if (commandLine.hasOption('?')) {
			forceHelp();
			return;
		} else { 
			this.fromCurrency = commandLine.getArgList().stream().map(String::toUpperCase).collect(Collectors.toList());
			if (fromCurrency.isEmpty()) {
				fromCurrency = Arrays.asList("BTC", "BTG", "BCH", "ZEC", "XMR", "ETH");
			}
		}
		toCurrency = commandLine.getOptionValue('t', DEFAULT_TO_CURRENCY).toUpperCase();
	}

	public static boolean matcher(String t) {
		return t.toLowerCase().startsWith(COMMAND.toLowerCase());
	}

	@Override
	public boolean isStandalone() {
		return true;
	}
	
	public String getFormattedCurrencyPair() {
		return "from currencies " + fromCurrency + " and to currency "+ toCurrency;
	}
}

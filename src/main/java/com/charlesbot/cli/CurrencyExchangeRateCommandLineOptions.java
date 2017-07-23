package com.charlesbot.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CurrencyExchangeRateCommandLineOptions extends Command {

	public static final String DEFAULT_BASE_CURRENCY = "USD";
	public static final String COMMAND = "!cq";
	public static final String COMMAND_SYNTAX = COMMAND + " <QUOTE_CURRENCY>";
	public static final String COMMAND_HEADER = "QUOTE_CURRENCY is the ISO 4217 code for the quote currency (e.g. EUR, JPY, BTC, ETH, etc.)";
	public static final String COMMAND_DESCRIPTION = "Returns the exchange rate for converting one unit of the base currency to the quote currency.";
	
	static Options options;
	
	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
				
		options.addOption( 
			Option.builder("b")
				.required(false)
				.longOpt("base")
				.hasArgs()
				.numberOfArgs(1)
				.argName("BASE_CURRENCY")
				.desc("is the ISO 4217 code for the base currency. Defaults to " + DEFAULT_BASE_CURRENCY + ".")
				.type(String.class)
				.build()
		);

	}

	public String baseCurrency;
	public String quoteCurrency;

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
		if (commandLine.getArgList().isEmpty() || commandLine.getArgList().size() > 1 || commandLine.hasOption('?')) {
			forceHelp();
			return;
		} else { 
			this.quoteCurrency = commandLine.getArgList().get(0).toUpperCase();
		}
		baseCurrency = commandLine.getOptionValue('b', DEFAULT_BASE_CURRENCY).toUpperCase();
	}

	public static boolean matcher(String t) {
		return t.toLowerCase().startsWith(COMMAND.toLowerCase());
	}

	@Override
	public boolean isStandalone() {
		return true;
	}
	
	public String getFormattedCurrencyPair() {
		return baseCurrency + "/" + quoteCurrency;
	}
}

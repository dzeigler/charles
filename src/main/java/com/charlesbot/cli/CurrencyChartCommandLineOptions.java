package com.charlesbot.cli;

import com.charlesbot.model.User;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CurrencyChartCommandLineOptions extends Command {

	public static final String COMMAND = "!cchart";
	public static final String COMMAND_SYNTAX = COMMAND + " <FROM_CURRENCY>";
	public static final String COMMAND_DESCRIPTION = "Returns the url for a <https://cryptohistory.org> chart of a currency's price history over time";
	public static final String COMMAND_HEADER = COMMAND_DESCRIPTION+"\nFROM_CURRENCY is the code for the currency to look up the price for (e.g. BTC, ETH, etc.).";
	private static final String DEFAULT_TIME_SPAN = "1m";
	
	static Options options;
	
	public static Map<String, CurrencyChartOption> SUPPORTED_TIME_SPANS = Collections.unmodifiableMap(Stream.of(
				new SimpleEntry<>("24h", new CurrencyChartOption("24h", "candlestick")),
				new SimpleEntry<>("1d", new CurrencyChartOption("24h", "candlestick")),
                new SimpleEntry<>("7d", new CurrencyChartOption("7d", "candlestick")),
                new SimpleEntry<>("30d", new CurrencyChartOption("30d", "candlestick")),
        		new SimpleEntry<>("1m", new CurrencyChartOption("30d", "candlestick")),
        		new SimpleEntry<>("1y", new CurrencyChartOption("1y", "dark"))
        		)
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    

	
	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
		options.addOption( 
			Option.builder("t")
				.required(false)
				.longOpt("time")
				.hasArg(true)
				.argName("TIME_SPAN")
				.desc("sets the time span for the chart in hours (24h), days (1d, 7d, 30d), months (1m), or years (1y). Default is " + DEFAULT_TIME_SPAN + ". TIME_SPAN = (24h|1d|7d|30d|1m|1y)")
				.type(String.class)
				.valueSeparator()
				.build()
		);
				
		options.addOption( 
			Option.builder("c")
				.required(false)
				.longOpt("compare")
				.hasArgs()
				.numberOfArgs(1)
				.argName("TO_CURRENCY")
				.desc("is the code for the currency the price should be in. Defaults to \"" + CurrencyQuoteCommandLineOptions.DEFAULT_TO_CURRENCY + "\".\"")
				.type(String.class)
				.valueSeparator()
				.build()
		);

	}

	public String fromCurrency;
	public String timeSpan;
	public String toCurrency;

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
	public void populateOptions(CommandLine commandLine, User user) {
		if (commandLine.getArgList().isEmpty() || commandLine.getArgList().size() > 1 || commandLine.hasOption('?')) {
			forceHelp();
			return;
		} else { 
			this.fromCurrency = commandLine.getArgList().get(0);
		}
		this.timeSpan = commandLine.getOptionValue('t', DEFAULT_TIME_SPAN);
		if (!SUPPORTED_TIME_SPANS.containsKey(timeSpan)) {
			addWarning("The time span provided is not supported by CryptoHistory! Using the default of " + DEFAULT_TIME_SPAN + ".");
			timeSpan = DEFAULT_TIME_SPAN;
		}
		this.toCurrency = commandLine.getOptionValue('c', CurrencyQuoteCommandLineOptions.DEFAULT_TO_CURRENCY);

	}

	public static boolean matcher(String t) {
		return t.toLowerCase().startsWith(COMMAND.toLowerCase());
	}

	@Override
	public boolean isStandalone() {
		return true;
	}
}

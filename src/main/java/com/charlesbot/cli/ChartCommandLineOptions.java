package com.charlesbot.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import com.newrelic.agent.deps.com.google.common.collect.Lists;

public class ChartCommandLineOptions extends Command {

	public static final String COMMAND = "!chart";
	public static final String COMMAND_SYNTAX = COMMAND + " <SYMBOL>";
	public static final String COMMAND_HEADER = "SYMBOL is the Yahoo Finance ticker for the stock or index";
	public static final String COMMAND_DESCRIPTION = "Returns the url for a chart of a stock's price history over time";
	private static final String DEFAULT_TIME_SPAN = "1y";
	private static final List<String> SUPPORTED_TIME_SPANS;
	
	static Options options;
	
	static {
		// populate supported time spans
		SUPPORTED_TIME_SPANS = Lists.newArrayList("1d","5d","1m","3m","6m","1y","2y","5y","my");
		
		
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
		options.addOption( 
			Option.builder("t")
				.required(false)
				.longOpt("time")
				.hasArg(true)
				.argName("TIME_SPAN")
				.desc("sets the time span for the chart in days (1d, 5d), months (1m, 3m, 6m), or years (1y, 2y, 5y, or my (maximum years)). Default is 1y. TIME_SPAN = (1d|5d|1m|3m|6m|1y|2y|5y|my)")
				.type(String.class)
				.valueSeparator()
				.build()
		);
				
		options.addOption( 
			Option.builder("c")
				.required(false)
				.longOpt("compare")
				.hasArgs()
				.numberOfArgs(Option.UNLIMITED_VALUES)
				.argName("SYMBOLS")
				.desc("compares the base symbol of this chart with this comma or space delimited list of up to nine additional symbols. SYMBOLS = SYMBOL[(, )+SYMBOL]]...")
				.type(String.class)
				.valueSeparator()
				.build()
		);

	}

	public String tickerSymbol;
	public String timeSpan;
	public List<String> symbolsToCompare = new ArrayList<>();

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
			this.tickerSymbol = commandLine.getArgList().get(0);
		}
		timeSpan = commandLine.getOptionValue('t', DEFAULT_TIME_SPAN);
		if (!SUPPORTED_TIME_SPANS.contains(timeSpan)) {
			addWarning("The time span provided is not supported by Yahoo! Using the default of " + DEFAULT_TIME_SPAN + ".");
			timeSpan = DEFAULT_TIME_SPAN;
		}
		if (commandLine.hasOption('c')) {
			List<String> compareValues = Arrays.asList(commandLine.getOptionValues('c'));
			this.symbolsToCompare = compareValues.stream()
					.map(x -> x.split("[,\\s]"))
					.flatMap(Arrays::stream)
					.filter(StringUtils::isNotBlank)
					.collect(Collectors.toList());
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

package com.charlesbot.cli;

import com.charlesbot.model.User;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

public class ChartCommandLineOptions extends Command {

	public static final String COMMAND = "!chart";
	public static final String COMMAND_SYNTAX = COMMAND + " <SYMBOL>";
	public static final String COMMAND_HEADER = "SYMBOL is the BigCharts ticker for the stock or index";
	public static final String COMMAND_DESCRIPTION = "Returns the url for a chart of a stock's price history over time";
	private static final String DEFAULT_TIME_SPAN = "1y";
	
	static Options options;
	
	public static Map<String, String> SUPPORTED_TIME_SPANS = Collections.unmodifiableMap(Stream.of(
                new SimpleEntry<>("1d", "1"),
                new SimpleEntry<>("2d", "2"),
                new SimpleEntry<>("5d", "3"),
        		new SimpleEntry<>("10d", "18"),
        		new SimpleEntry<>("1m", "4"),
        		new SimpleEntry<>("2m", "5"),
        		new SimpleEntry<>("3m", "6"),
        		new SimpleEntry<>("6m", "7"),
        		new SimpleEntry<>("ytd", "19"),
        		new SimpleEntry<>("1y", "8"),
        		new SimpleEntry<>("2y", "9"),
        		new SimpleEntry<>("3y", "10"),
        		new SimpleEntry<>("4y", "11"),
        		new SimpleEntry<>("5y", "12"),
        		new SimpleEntry<>("10y", "13"),
        		new SimpleEntry<>("my", "20")
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
				.desc("sets the time span for the chart in days (1d, 2d, 5d, 10d), months (1m, 2m, 3m, 6m), or years (1y, 2y, 3y, 4y, 5y, 10y, or my (maximum years)) or year-to-date (ytd). Default is 1y. TIME_SPAN = (1d|2d|5d|10d|1m|2m|3m|6m|1y|2y|3y|4y|5y|10y|my|ytd)")
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
	public void populateOptions(CommandLine commandLine, User user) {
		if (commandLine.getArgList().isEmpty() || commandLine.getArgList().size() > 1 || commandLine.hasOption('?')) {
			forceHelp();
			return;
		} else { 
			this.tickerSymbol = commandLine.getArgList().get(0);
		}
		timeSpan = commandLine.getOptionValue('t', DEFAULT_TIME_SPAN);
		if (!SUPPORTED_TIME_SPANS.containsKey(timeSpan)) {
			addWarning("The time span provided is not supported by IEX! Using the default of " + DEFAULT_TIME_SPAN + ".");
			timeSpan = DEFAULT_TIME_SPAN;
		}
		if (commandLine.hasOption('c')) {
			List<String> compareValues = Arrays.asList(commandLine.getOptionValues('c'));
			this.symbolsToCompare = compareValues.stream()
					.map(x -> x.split("[,\\p{Z}]"))
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

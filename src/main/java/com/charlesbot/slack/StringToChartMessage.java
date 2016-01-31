package com.charlesbot.slack;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class StringToChartMessage implements Converter<String, ChartMessage> {

	private static final String DEFAULT_TIME_SPAN = "1y";
	private CommandLineParser parser;
	private Options options;

	public StringToChartMessage(CommandLineParser parser, Options options) {
		this.parser = parser;
		this.options = options;
	}

	public StringToChartMessage() {
		// create the command line parser
		parser = new DefaultParser();

		// configure options
		configureCommandLineOptions();
	}

	private void configureCommandLineOptions() {
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

	@Override
	public ChartMessage convert(String text) {
		ChartMessage chartMessage = new ChartMessage();
		try {
			CommandLine command = parser.parse(options, text.split("\\s+"));
			List<String> argList = command.getArgList();
			if (argList.size() != 2 || command.hasOption('?')) {
				String helpMessage = generateHelpMessage();
				chartMessage.setText("```"+helpMessage+"```");
			} else {
				String timeSpan = DEFAULT_TIME_SPAN;
				String compare = "";
				if (command.hasOption('t')) {
					timeSpan = command.getOptionValue('t', DEFAULT_TIME_SPAN);
				}
				if (command.hasOption('c')) {
					List<String> compareValues = Arrays.asList(command.getOptionValues('c'));
					compare = compareValues.stream()
							.map(x -> x.split("[,\\s]"))
							.flatMap(Arrays::stream)
							.filter(StringUtils::isNotBlank)	
							.collect(Collectors.joining(","));
				}
				String symbol = command.getArgList().get(1);
				
				String url = MessageFormat.format("<http://chart.finance.yahoo.com/z?s={0}&t={1}&c={2}&q=l&z=l&p=s&a=v&cb={3,number,#}>", symbol, timeSpan, compare, System.currentTimeMillis());
				chartMessage.setText(url);
			}
		} catch (ParseException e) {
			String failureMessage = e.getMessage();
			String helpMessage = generateHelpMessage();
			chartMessage.setText("```"+failureMessage + "\n" + helpMessage+"```");
		}
		return chartMessage;
	}

	private String generateHelpMessage() {
		HelpFormatter formatter = new HelpFormatter();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		formatter.printHelp(pw, 80, "!chart <SYMBOL>", "SYMBOL is the Yahoo Finance ticker for the stock or index", options, 0, 3, "", true);
		String string = sw.toString();
		return string;
	}

}

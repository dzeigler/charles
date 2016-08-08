package com.charlesbot.cli;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CommandLineProcessorTest {

	CommandLineProcessor processor = new CommandLineProcessor();
	@Test
	public void unsupportedCommand() {
		Command command = processor.process("safasdf", "userid", "botname");
		
		assertThat(command, is(nullValue()));
	}

	@Test
	public void quoteMessageWithOneTickerSymbol() {
		Command command = processor.process("!q tsla", "userid", "botname");
		
		assertThat(command.getName(), is(QuoteCommandLineOptions.COMMAND));
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasItem("tsla"));
		assertThat(qCommand.isHelp(), is(false));
	}

	@Test
	public void quoteMessageWithMoreThanOneTickerSymbol() {
		Command command = processor.process("!q tsla amzn goog", "userid", "botname");
		
		assertThat(command.getName(), is(QuoteCommandLineOptions.COMMAND));
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(3));
		assertThat(qCommand.tickerSymbols, hasItem("tsla"));
		assertThat(qCommand.tickerSymbols, hasItem("goog"));
		assertThat(qCommand.tickerSymbols, hasItem("amzn"));
		assertThat(qCommand.isHelp(), is(false));
	}
	
	@Test
	public void quoteWithNoTicker() {
		Command command = processor.process("!q", "userid", "botname");
		
		assertThat(command.getName(), is(QuoteCommandLineOptions.COMMAND));
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(0));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void quoteWithHelp() {
		Command command = processor.process("!q -?", "userid", "botname");
		
		assertThat(command.getName(), is(QuoteCommandLineOptions.COMMAND));
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(0));
		assertThat(qCommand.isHelp(), is(true));
	}

	@Test
	public void quoteWithHelpAndTickerSymbol() {
		Command command = processor.process("!q -? tsla", "userid", "botname");
		
		assertThat(command.getName(), is(QuoteCommandLineOptions.COMMAND));
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(0));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void statsMessageWithOneTickerSymbol() {
		Command command = processor.process("!stats tsla", "userid", "botname");
		
		assertThat(command.getName(), is(StatsCommandLineOptions.COMMAND));
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasItem("tsla"));
		assertThat(qCommand.isHelp(), is(false));
	}

	@Test
	public void statsMessageWithMoreThanOneTickerSymbol() {
		Command command = processor.process("!stats tsla amzn goog", "userid", "botname");
		
		assertThat(command.getName(), is(StatsCommandLineOptions.COMMAND));
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(3));
		assertThat(qCommand.tickerSymbols, hasItem("tsla"));
		assertThat(qCommand.tickerSymbols, hasItem("goog"));
		assertThat(qCommand.tickerSymbols, hasItem("amzn"));
		assertThat(qCommand.isHelp(), is(false));
	}
	
	@Test
	public void statsWithNoTicker() {
		Command command = processor.process("!stats", "userid", "botname");
		
		assertThat(command.getName(), is(StatsCommandLineOptions.COMMAND));
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(0));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void statsWithHelp() {
		Command command = processor.process("!stats -?", "userid", "botname");
		
		assertThat(command.getName(), is(StatsCommandLineOptions.COMMAND));
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(0));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void statsWithHelpAndTickerSymbol() {
		Command command = processor.process("!stats -? tsla", "userid", "botname");
		
		assertThat(command.getName(), is(StatsCommandLineOptions.COMMAND));
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols, hasSize(0));
		assertThat(qCommand.isHelp(), is(true));
	}

	@Test
	public void chartMessageWithOneTickerSymbol() {
		Command command = processor.process("!chart tsla", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is("tsla"));
		assertThat(qCommand.isHelp(), is(false));
	}

	@Test
	public void chartMessageWithMoreThanOneTickerSymbol() {
		Command command = processor.process("!chart tsla amzn goog", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is(nullValue()));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void chartWithNoTicker() {
		Command command = processor.process("!chart", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is(nullValue()));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void chartWithHelp() {
		Command command = processor.process("!chart -?", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is(nullValue()));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void chartWithHelpAndTickerSymbol() {
		Command command = processor.process("!chart -? tsla", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is(nullValue()));
		assertThat(qCommand.isHelp(), is(true));
	}
	
	@Test
	public void chartWithTimeSpan() {
		Command command = processor.process("!chart -tmy tsla", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is("tsla"));
		assertThat(qCommand.timeSpan, is("my"));
		assertThat(qCommand.isHelp(), is(false));
		assertThat(qCommand.getWarnings(), hasSize(0));
		assertThat(qCommand.getErrors(), hasSize(0));
	}
	
	@Test
	public void chartWithUnknownTimeSpan() {
		Command command = processor.process("!chart -t22y tsla", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is("tsla"));
		assertThat(qCommand.timeSpan, is("1y"));
		assertThat(qCommand.isHelp(), is(false));
		assertThat(qCommand.getWarnings(), hasSize(1));
		assertThat(qCommand.getErrors(), hasSize(0));
	}
	
	@Test
	public void chartWithComparisonTickerSymbols() {
		Command command = processor.process("!chart tsla -c goog", "userid", "botname");
		
		assertThat(command.getName(), is(ChartCommandLineOptions.COMMAND));
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol, is("tsla"));
		assertThat(qCommand.symbolsToCompare, hasItem("goog"));
		assertThat(qCommand.isHelp(), is(false));
		assertThat(qCommand.getWarnings(), hasSize(0));
		assertThat(qCommand.getErrors(), hasSize(0));
	}
	
	
}

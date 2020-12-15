package com.charlesbot.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.charlesbot.model.UserRepository;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
public class CommandLineProcessorTest {

	@Mock
	private UserRepository userRepository;
	private CommandLineProcessor processor;

	@BeforeEach
	public void setup() {
		when(userRepository.findById(anyString())).thenReturn(Optional.empty());
		processor = new CommandLineProcessor(userRepository);
	}

	@Test
	public void unsupportedCommand() {
		Command command = processor.process("safasdf", "userid", "botname");
		
		assertThat(command).isInstanceOf(HelpCommandLineOptions.class);
	}

	@Test
	public void quoteMessageWithOneTickerSymbol() {
		Command command = processor.process("!q tsla", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(QuoteCommandLineOptions.COMMAND);
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).contains("tsla");
		assertThat(qCommand.isHelp()).isFalse();
	}

	@Test
	public void quoteMessageWithMoreThanOneTickerSymbol() {
		Command command = processor.process("!q tsla amzn goog", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(QuoteCommandLineOptions.COMMAND);
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).hasSize(3);
		assertThat(qCommand.tickerSymbols).containsOnly("tsla", "goog", "amzn");
		assertThat(qCommand.isHelp()).isFalse();
	}
	
	@Test
	public void quoteMessageWithNbsp() {
		Command command = processor.process("!q tsla amzn goog", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(QuoteCommandLineOptions.COMMAND);
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).hasSize(3);
		assertThat(qCommand.tickerSymbols).containsOnly("tsla", "goog", "amzn");
		assertThat(qCommand.isHelp()).isFalse();
	}
	
	@Test
	public void quoteWithNoTicker() {
		Command command = processor.process("!q", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(QuoteCommandLineOptions.COMMAND);
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).isEmpty();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void quoteWithHelp() {
		Command command = processor.process("!q -?", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(QuoteCommandLineOptions.COMMAND);
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).isEmpty();
		assertThat(qCommand.isHelp()).isTrue();
	}

	@Test
	public void quoteWithHelpAndTickerSymbol() {
		Command command = processor.process("!q -? tsla", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(QuoteCommandLineOptions.COMMAND);
		
		QuoteCommandLineOptions qCommand = (QuoteCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).isEmpty();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void statsMessageWithOneTickerSymbol() {
		Command command = processor.process("!stats tsla", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(StatsCommandLineOptions.COMMAND);
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).containsOnly("tsla");
		assertThat(qCommand.isHelp()).isFalse();
	}

	@Test
	public void statsMessageWithMoreThanOneTickerSymbol() {
		Command command = processor.process("!stats tsla amzn goog", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(StatsCommandLineOptions.COMMAND);
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).containsOnly("tsla", "goog", "amzn");
		assertThat(qCommand.isHelp()).isFalse();
	}
	
	@Test
	public void statsWithNoTicker() {
		Command command = processor.process("!stats", "userid", "botname");
		
		assertThat(command.getName()).isEqualTo(StatsCommandLineOptions.COMMAND);
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).isEmpty();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void statsWithHelp() {
		Command command = processor.process("!stats -?", "userid", "botname");

		assertThat(command.getName()).isEqualTo(StatsCommandLineOptions.COMMAND);
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).isEmpty();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void statsWithHelpAndTickerSymbol() {
		Command command = processor.process("!stats -? tsla", "userid", "botname");

		assertThat(command.getName()).isEqualTo(StatsCommandLineOptions.COMMAND);
		
		StatsCommandLineOptions qCommand = (StatsCommandLineOptions) command;
		assertThat(qCommand.tickerSymbols).isEmpty();
		assertThat(qCommand.isHelp()).isTrue();
	}

	@Test
	public void chartMessageWithOneTickerSymbol() {
		Command command = processor.process("!chart tsla", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).contains("tsla");
		assertThat(qCommand.isHelp()).isFalse();
	}

	@Test
	public void chartMessageWithMoreThanOneTickerSymbol() {
		Command command = processor.process("!chart tsla amzn goog", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).isNull();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void chartWithNoTicker() {
		Command command = processor.process("!chart", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).isNull();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void chartWithHelp() {
		Command command = processor.process("!chart -?", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).isNull();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void chartWithHelpAndTickerSymbol() {
		Command command = processor.process("!chart -? tsla", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).isNull();
		assertThat(qCommand.isHelp()).isTrue();
	}
	
	@Test
	public void chartWithTimeSpan() {
		Command command = processor.process("!chart -tmy tsla", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).isEqualTo("tsla");
		assertThat(qCommand.timeSpan).isEqualTo("my");
		assertThat(qCommand.isHelp()).isFalse();
		assertThat(qCommand.getWarnings()).isEmpty();
		assertThat(qCommand.getErrors()).isEmpty();
	}
	
	@Test
	public void chartWithUnknownTimeSpan() {
		Command command = processor.process("!chart -t22y tsla", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).isEqualTo("tsla");
		assertThat(qCommand.timeSpan).isEqualTo("1y");
		assertThat(qCommand.isHelp()).isFalse();
		assertThat(qCommand.getWarnings()).hasSize(1);
		assertThat(qCommand.getErrors()).isEmpty();
	}
	
	@Test
	public void chartWithComparisonTickerSymbols() {
		Command command = processor.process("!chart tsla -c goog", "userid", "botname");

		assertThat(command.getName()).isEqualTo(ChartCommandLineOptions.COMMAND);
		
		ChartCommandLineOptions qCommand = (ChartCommandLineOptions) command;
		assertThat(qCommand.tickerSymbol).isEqualTo("tsla");
		assertThat(qCommand.symbolsToCompare).contains("goog");
		assertThat(qCommand.isHelp()).isFalse();
		assertThat(qCommand.getWarnings()).isEmpty();
		assertThat(qCommand.getErrors()).isEmpty();
	}
	
	
}

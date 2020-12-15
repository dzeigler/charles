package com.charlesbot.cli;

import com.charlesbot.model.User;
import com.charlesbot.model.UserRepository;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineProcessor {

	private static final Logger log = LoggerFactory.getLogger(CommandLineProcessor.class);
	private final UserRepository userRepository;

	private CommandLineParser parser;

	public static final Map<Predicate<String>, Supplier<Command>> supportedCommands = new LinkedHashMap<>();
	static {
		supportedCommands.put(QuoteCommandLineOptions::matcher, QuoteCommandLineOptions::new);
		supportedCommands.put(ChartCommandLineOptions::matcher, ChartCommandLineOptions::new);
		supportedCommands.put(StatsCommandLineOptions::matcher, StatsCommandLineOptions::new);
		supportedCommands.put(AddToListCommandLineOptions::matcher, AddToListCommandLineOptions::new);
		supportedCommands.put(RemoveFromListCommandLineOptions::matcher, RemoveFromListCommandLineOptions::new);
		supportedCommands.put(ListCommandLineOptions::matcher, ListCommandLineOptions::new);
		supportedCommands.put(ListQuoteCommandLineOptions::matcher, ListQuoteCommandLineOptions::new);
		supportedCommands.put(ListStatsCommandLineOptions::matcher, ListStatsCommandLineOptions::new);
		//supportedCommands.put(CurrencyExchangeRateCommandLineOptions::matcher, CurrencyExchangeRateCommandLineOptions::new);
		supportedCommands.put(CurrencyQuoteCommandLineOptions::matcher, CurrencyQuoteCommandLineOptions::new);
		supportedCommands.put(CurrencyChartCommandLineOptions::matcher, CurrencyChartCommandLineOptions::new);
		supportedCommands.put(HelpCommandLineOptions::matcher, HelpCommandLineOptions::new);
	}
	
	
	public CommandLineProcessor(UserRepository userRepository) {
		
		// create the command line parser
		parser = new DefaultParser();

		this.userRepository = userRepository;
	}

	public Command process(String commandString, String senderUserId, String botUserName) {
		Command command = null;

		// tokenize the command
		String[] tokens = commandString.split("\\p{Z}+");
		
			
		Supplier<Command> supplier = supportedCommands.keySet()
			.stream()
			.filter(c -> c.test(commandString))
			.findFirst()
			.map(supportedCommands::get)
			.orElse(new Supplier<Command>() {
				@Override
				public Command get() {
					return new HelpCommandLineOptions();
				}
			})
			;
		
		if (supplier.get() != null) {
			command = supplier.get();
			
			String[] arguments = new String[tokens.length-1];
			if (tokens.length > 1) {
				Arrays.asList(tokens).subList(1, tokens.length).toArray(arguments);
			}
			try {
				command.setBotUsername(botUserName);
				CommandLine commandLine = parser.parse(command.getOptions(), arguments);

				User user = userRepository.findById(senderUserId).orElseGet(() -> {
					User u = new User();
					u.userId = senderUserId;
					return userRepository.save(u);
				});
				command.populateOptions(commandLine, user);
				
			} catch (Exception e) {
				log.error("An error occurred while processing [{}] {}", e, commandString);
				command.forceHelp();
			}
		}
		
		// return the output of the command
		return command;
	}

	public static String generateHelpMessage(Command command) {
		HelpFormatter formatter = new HelpFormatter();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		formatter.printHelp(pw, 80, command.getSyntax(), command.getDescriptionHeader(), command.getOptions(), 0, 3, "", true);
		String string = sw.toString();
		return string;
	}
	
	public static String generateHelpMessage(String description) {
		HelpFormatter formatter = new HelpFormatter();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		formatter.printHelp(pw, 80, null, description, null, 0, 3, "", true);
		String string = sw.toString();
		return string;
	}
	
}

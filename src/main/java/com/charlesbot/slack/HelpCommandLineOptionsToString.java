package com.charlesbot.slack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.charlesbot.cli.Command;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.HelpCommandLineOptions;
import com.google.common.base.Joiner;

@Component
public class HelpCommandLineOptionsToString implements Converter<HelpCommandLineOptions, String> {

	public HelpCommandLineOptionsToString() {
	}

	@Override
	public String convert(HelpCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		output.append("```");
		
		List<String> messages = new ArrayList<>();
		for (Supplier<Command> p : CommandLineProcessor.supportedCommands.values()) {
			StringBuilder commandHelp= new StringBuilder();
			StringBuilder commandHelpWithDescription= new StringBuilder();
			Command c = p.get();
			if (!c.isStandalone()) {
				commandHelp.append('@'+options.getBotUsername()).append(" ");
			}
			commandHelp.append(c.getName());
			commandHelpWithDescription.append(String.format("%1$-20s", commandHelp.toString())).append(' ').append(c.getDescription());
			messages.add(commandHelpWithDescription.toString());
		}
		Collections.sort(messages);
		output.append(Joiner.on('\n').join(messages));
		output.append("```");
		return output.toString();
	}

}

package com.charlesbot.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public abstract class Command {

	private boolean help;
	private List<String> warnings = new ArrayList<>();
	private List<String> errors = new ArrayList<>();
	private String botUsername;
	
	public boolean isHelp() {
		return help;
	}
	
	void forceHelp() {
		help = true;
	}
	
	protected void addWarning(String warning) {
		warnings.add(warning);
	}
	
	public List<String> getWarnings() {
		return warnings;
	}
	
	protected void addError(String error) {
		errors.add(error);
		forceHelp();
	}
	
	public List<String> getErrors() {
		return errors;
	}

	public abstract String getName();
	public abstract String getDescription();
	public abstract String getDescriptionHeader();
	public abstract String getSyntax();
	public abstract boolean isStandalone();
	public abstract Options getOptions();
//	public abstract Predicate<String> matcher();
	

	public abstract void populateOptions(CommandLine commandLine, String userId);

	public String getBotUsername() {
		return botUsername;
	}

	public void setBotUsername(String botUsername) {
		this.botUsername = botUsername;
	}

}

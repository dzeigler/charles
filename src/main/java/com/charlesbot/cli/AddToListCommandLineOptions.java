package com.charlesbot.cli;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import com.charlesbot.model.Transaction;
import com.google.common.base.Splitter;

public class AddToListCommandLineOptions extends Command {

	public static final String COMMAND = "add";
	public static final String COMMAND_SYNTAX = COMMAND + " <LIST_NAME> <SYMBOL>[,<QUANTITY>,<PRICE>[,<DATE>]]...";
	public static final String COMMAND_HEADER = 
			"LIST_NAME is the name watch list being modified\n"
			+ "SYMBOL is the IEX shares included in the transaction\n"
			+ "PRICE is the amount of money paid per share\n" 
			+ "DATE should be in the format yyyy-MM-dd; defaults to the current date if omitted";
	public static final String COMMAND_DESCRIPTION = "Adds ticker symbols or transactions to a list";
	public static final String COMMAND_PATTERN = "^@\\w+:?\\s*add.*";

	static Options options;

	static {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");

	}
	
	public String watchListName;
	public List<Transaction> transactions = new ArrayList<>();
	public String userId;
	
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
	public void populateOptions(CommandLine commandLine, String userId) {
		if (commandLine.getArgList().isEmpty()) {
			forceHelp();
		} else if (commandLine.getArgList().size() < 3) {
			forceHelp();
		}

		this.userId = userId;
		watchListName = commandLine.getArgList().get(1);
		
		for (String transactionString : commandLine.getArgList().subList(2, commandLine.getArgList().size())) {
			List<String> transactionTokens = Splitter.on(',').splitToList(transactionString);
			Transaction transaction = new Transaction();
			this.transactions.add(transaction);
			transaction.setSymbol(transactionTokens.get(0));
			if (transactionTokens.size() != 1 && transactionTokens.size() != 3 && transactionTokens.size() != 4) {
				addError("The format for this entry is not recognized: " + transactionString);
				forceHelp();
			} else if (transactionTokens.size() == 3 || transactionTokens.size() == 4) {
				String quantityString = transactionTokens.get(1);
				try {
					BigDecimal quantity = new BigDecimal(quantityString);
					transaction.quantity = quantity;
				} catch (NumberFormatException e) {
					addError(quantityString + " isn't a number so I can't used it as a quantity for " + transaction.getSymbol());
				}
				
				String priceString = transactionTokens.get(2);
				try {
					BigDecimal price = new BigDecimal(priceString);
					transaction.price = price;
				} catch (NumberFormatException e) {
					addError(priceString + " isn't a number so I can't use it as a price for " + transaction.getSymbol());
				}
				
				if (transactionTokens.size() > 3) {
					String dateString = transactionTokens.get(3);
				
					try {
						LocalDate date = LocalDate.now();
						if (StringUtils.isNotEmpty(dateString)) {
							date = LocalDate.parse(dateString, Transaction.DATE_FORMATTER);
						}
						transaction.date = date;
					} catch (DateTimeParseException e) {
						addError("The date should be in the format yyyy-MM-dd for " + transaction.getSymbol());
					}
				}
			}
			
		}
		
	}
	
	public static boolean matcher(String t) {
		return t.toLowerCase().matches(COMMAND_PATTERN.toLowerCase());
	}

	@Override
	public boolean isStandalone() {
		return false;
	}

}

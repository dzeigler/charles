package com.charlesbot.slack.listeners;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;

import com.charlesbot.model.Transaction;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.charlesbot.slack.ChartMessage;
import com.charlesbot.slack.PortfolioQuoteMessage;
import com.charlesbot.slack.QuoteMessage;
import com.charlesbot.slack.StatsMessage;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackPersona;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

public class CharlesCommandListener implements SlackMessagePostedListener {

	private static final Logger logger = LoggerFactory.getLogger(CharlesCommandListener.class);
	private List<String> keywords;
	private ConversionService conversionService;
	private WatchListRepository repo;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	
	CharlesCommandListener() {
		keywords = Lists.newArrayList("!q", "!stats", "!chart", "!pf");
	}
	
	public CharlesCommandListener(ConversionService conversionService, WatchListRepository repo) {
		this();
		this.conversionService = conversionService;
		this.repo = repo;
	}
	
	@Override
	public void onEvent(SlackMessagePosted event, SlackSession session) {
		String messageContent = event.getMessageContent();
		SlackPersona sessionPersona = session.sessionPersona();
		String userId = sessionPersona.getId();
		String userIdMention = "<@"+userId+">";
		String userNameMention = "@"+sessionPersona.getUserName();
		if (isCharlesCommand(messageContent, userIdMention) && !event.getSender().getId().equals(userId)) {
			SlackChannel channel = event.getChannel();
			List<String> tokens = new ArrayList<>(Splitter.on(" ").omitEmptyStrings().splitToList(messageContent));
			String reply = null;
			if (tokens.size() > 1) {
				if (tokens.get(0).equals("!q")) {
					reply = conversionService.convert(tokens.subList(1, tokens.size()), QuoteMessage.class).getText();
				} else if (tokens.get(0).equals("!stats")) {
					reply = conversionService.convert(tokens.subList(1, tokens.size()), StatsMessage.class).getText();
				} else if (tokens.get(0).equals("!chart")) {
					reply = conversionService.convert(messageContent, ChartMessage.class).getText();
				} else if (tokens.get(0).equals(userIdMention) && tokens.get(1).equals("add")) {
					WatchList watchList = repo.findByUserIdAndName(event.getSender().getId(), tokens.get(2));
					if (watchList == null) {
						watchList = new WatchList();
						watchList.name = tokens.get(2);
						watchList.transactions = new ArrayList<>();
						watchList.userId = event.getSender().getId();
					}
					Transaction t = new Transaction();
					ArrayList<String> transactionTokens = new ArrayList<>(Splitter.on(",").omitEmptyStrings().splitToList(tokens.get(3)));
					t.symbol = transactionTokens.get(0);
					boolean foundError = false;
					if (transactionTokens.size() == 4) {
						String quantityString = transactionTokens.get(1);
						try {
							BigDecimal quantity = new BigDecimal(quantityString);
							t.quantity = quantity;
						} catch (NumberFormatException e) {
							session.sendMessage(channel, quantityString + " isn't a number so I can't used it as a quantity");
							foundError = true;
						}
						
						String priceString = transactionTokens.get(2);
						try {
							BigDecimal price = new BigDecimal(priceString);
							t.price = price;
						} catch (NumberFormatException e) {
							session.sendMessage(channel, priceString + " isn't a number so I can't use it as a price");
							foundError = true;
						}
						
						String dateString = transactionTokens.get(3);
						try {
							LocalDate date = LocalDate.parse(dateString, formatter);
							t.date = date;
						} catch (DateTimeParseException e) {
							session.sendMessage(channel, "The date should be in the format yyyy-MM-dd");
							foundError = true;
						}
					}
					if (foundError == false) {
						watchList.transactions.add(t);
						repo.save(watchList);
						reply = "Added " + t.symbol + " to list " + watchList.name;
					}
					
				} else if (tokens.get(0).equals(userIdMention) && (tokens.get(1).equals("rm") || tokens.get(1).equals("del"))) {
					WatchList watchList = repo.findByUserIdAndName(event.getSender().getId(), tokens.get(2));
					if (watchList == null) {
						reply = "I can't find a list named " + tokens.get(2) + " for you.";
					} else {
						if (tokens.size() == 3) {
							reply = "Use the -f option if you want to delete the entire list";
						} else {
							if (tokens.get(3).equals("-f")) {
								repo.delete(watchList);
								reply = watchList.name + " has been deleted";
							} else {
								Transaction transactionToRemove = null;
								for (Transaction t : watchList.transactions) {
									if (t.symbol.equals(tokens.get(3))) {
										transactionToRemove = t;
										break;
									}
								}
								if (transactionToRemove != null) {
									watchList.transactions.remove(transactionToRemove);
									if (watchList.transactions.isEmpty()) {
										repo.delete(watchList);
										reply = "Removing " + transactionToRemove.symbol + " and deleting the empty list " + watchList.name;
									} else {
										repo.save(watchList);
										reply = "Removing " + transactionToRemove.symbol + " from list " + watchList.name;
									}
									
								} else {
									reply = "I can't do that."; 
								}
							}
						}
					}
				} else if (tokens.get(0).equals(userIdMention) && tokens.get(1).equals("ls")) {
					if (tokens.size() == 2) {
						List<WatchList> watchLists = repo.findByUserId(event.getSender().getId());
						reply = watchLists.stream()
					        .map(wl -> wl.name)
					        .collect(Collectors.joining("\t"));
					} else {
						WatchList watchList = repo.findByUserIdAndName(event.getSender().getId(), tokens.get(2));
					
						if (watchList == null) {
							reply = "I can't find a list named " + tokens.get(2) + " for you.";
						} else {
							reply = watchList.transactions.stream()
						        .map(tx -> "[" + watchList.transactions.indexOf(tx) + "] - " + tx.toString(","))
						        .sorted()
						        .collect(Collectors.joining("\n")); 
						}
					}
				} else if (tokens.get(0).equals(userIdMention) && tokens.get(1).equals("q")) {
					WatchList watchList = repo.findByUserIdAndName(event.getSender().getId(), tokens.get(2));
					if (watchList == null) {
						reply = "I can't find a list named " + tokens.get(2) + " for you.";
					} else {
						if (watchList.transactions != null && !watchList.transactions.isEmpty()) {
							List<String> symbols = watchList.transactions.stream()
						        .filter(tx -> tx != null && tx.symbol != null)
						        .map(tx -> tx.symbol)
						        .distinct()
						        .collect(Collectors.toList());
							if (symbols.isEmpty()) {
								reply = "This list is empty";
							} else {
								reply = conversionService.convert(symbols, QuoteMessage.class).getText();
							}
						}
						 
					}
				} else if (tokens.get(0).equals(userIdMention) && tokens.get(1).equals("stats")) {
					WatchList watchList = repo.findByUserIdAndName(event.getSender().getId(), tokens.get(2));
					if (watchList == null) {
						reply = "I can't find a list named " + tokens.get(2) + " for you.";
					} else {
						if (watchList.transactions != null && !watchList.transactions.isEmpty()) {
							String transactionString = watchList.transactions.stream()
						        .filter(tx -> tx != null && tx.symbol != null)
						        .map(tx -> tx.toString(","))
						        .collect(Collectors.joining(" "));
							if (transactionString.isEmpty()) {
								reply = "This list is empty";
							} else {
								reply = conversionService.convert(transactionString, PortfolioQuoteMessage.class).getText();
							}
						}
						 
					}
				} else {
					reply = userNameMention + " add listName symbol[,quantity,price,date]\n";
					reply += userNameMention + " rm listName [-f|symbol]\n";
					reply += userNameMention + " ls [listName]\n";
					reply += userNameMention + " q listName\n";
					reply += userNameMention + " stats listName\n";
				}
				
			}
			
			if (StringUtils.isNotBlank(reply)) {
				session.sendMessage(channel, reply);
			}
		}		

	}
	
	private boolean isCharlesCommand(String command, String userIdMention) {
		
		for (String word : keywords) {
			if (command.startsWith(word)) {
				return true;
			}
		}
		
		if (command.startsWith(userIdMention)) {
			return true;
		}
		return false;
	}
	
}

package com.charlesbot.slack;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.charlesbot.model.StockQuotes;
import com.charlesbot.yahoo.YahooFinanceClient;
import com.google.common.base.Splitter;

@RestController
public class SlackRestController {

	private static final Logger log = LoggerFactory.getLogger(SlackRestController.class);
	
	@Inject
	private YahooFinanceClient YahooFinanceClient;
	@Inject
	private ConversionService conversionService;
	@Inject SlackPostMessageSender slackMessageSender;
	
	@RequestMapping(value = "/")
	@ResponseBody
	String alive() {
		return "Nothing to see here!";
	}
	
	@RequestMapping(value = "/q", method = { RequestMethod.POST }, consumes = { "text/plain", "application/*" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	SlackIncomingMessage quote(@RequestBody MultiValueMap<String, String> slackOutgoingMessage)
			throws UnsupportedEncodingException {
		log.debug("Request with outgoing slack message={}", slackOutgoingMessage);
		String triggerWord = slackOutgoingMessage.get("trigger_word").get(0);
		List<String> tokens = parseText(slackOutgoingMessage.get("text").get(0), triggerWord);
		SlackIncomingMessage slackIncomingMessage = null;
		// ignore trigger words with no args
		if (!tokens.isEmpty()) {
			// Remove duplicate symbols
			List<String> symbols = new ArrayList<>(new LinkedHashSet<>(tokens));
			
			switch (triggerWord) {
			case "!q":
				slackIncomingMessage = getStockQuotes(symbols);
				break;
			case "!stats":
				slackIncomingMessage = getStockStats(symbols);
				break;
			}
		}
		return slackIncomingMessage;
	}

	@RequestMapping(value = "/chart", method = { RequestMethod.POST }, consumes = { "text/plain", "application/*" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	SlackIncomingMessage chart(@RequestBody MultiValueMap<String, String> slackOutgoingMessage) throws UnsupportedEncodingException {
		log.debug("Chart request with outgoing slack message={}", slackOutgoingMessage);
		
		String text = slackOutgoingMessage.get("text").get(0);
		ChartMessage chartMessage = conversionService.convert(text, ChartMessage.class);
		return chartMessage;
	}
	
	@RequestMapping(value = "/pfquote", method = { RequestMethod.POST }, consumes = { "text/plain", "application/*" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	SlackIncomingMessage portfolioQuote(@RequestBody MultiValueMap<String, String> slackOutgoingMessage) throws UnsupportedEncodingException {
		log.debug("Portfolio quote request with outgoing slack message={}", slackOutgoingMessage);
		
		String text = slackOutgoingMessage.get("text").get(0);
		PortfolioQuoteMessage message = conversionService.convert(text, PortfolioQuoteMessage.class);
		return message;
	}
	
	private SlackIncomingMessage getStockQuotes(List<String> symbols) {
		Optional<StockQuotes> stockQuotes = YahooFinanceClient.getStockQuotes(symbols);
		log.debug("Returned stock quote {}", stockQuotes);
		SlackIncomingMessage message = new QuoteMessage();
		if (stockQuotes.isPresent()) {
			message = conversionService.convert(stockQuotes.get(), QuoteMessage.class);
		} else {
			message = new QuoteMessage();
			message.setText("An error occurred. Make sure the ticker symbol is valid for Yahoo Finance.");
		}
		return message;
	}
	
	private SlackIncomingMessage getStockStats(List<String> symbols) {
		Optional<StockQuotes> stockQuotes = YahooFinanceClient.getStockQuotes(symbols);
		log.debug("Returned stock quote {}", stockQuotes);
		SlackIncomingMessage message = new QuoteMessage();
		if (stockQuotes.isPresent()) {
			message = conversionService.convert(stockQuotes.get(), StatsMessage.class);
		} else {
			message = new QuoteMessage();
			message.setText("An error occurred. Make sure the ticker symbol is valid for Yahoo Finance");
		}
		return message;
	}

	private List<String> parseText(String command, String triggerWord) {
		List<String> tokens = new ArrayList<>(Splitter.on(" ").omitEmptyStrings().splitToList(command));

		// remove the trigger word from the tokens list
		if (!tokens.isEmpty() && tokens.get(0).equals(triggerWord)) {
			tokens.remove(0);
		}
		return tokens;
	}
}
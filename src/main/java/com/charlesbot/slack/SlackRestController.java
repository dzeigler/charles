package com.charlesbot.slack;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
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

import com.charlesbot.google.GoogleFinanceClient;
import com.charlesbot.model.StockQuotes;
import com.charlesbot.yahoo.YahooFinanceClient;
import com.google.common.base.Splitter;

@RestController
public class SlackRestController {

	private static final Logger log = LoggerFactory.getLogger(SlackRestController.class);
	@Inject
	private YahooFinanceClient yahooFinanceClient;
	@Inject
	private GoogleFinanceClient googleFinanceClient;
	@Inject
	private ConversionService conversionService;

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
			switch (triggerWord) {
			case "!q":
				slackIncomingMessage = getStockQuotes(tokens);
				break;
			case "!chart":
				slackIncomingMessage = chart(tokens);
				break;
			}
		}
		return slackIncomingMessage;
	}

	private SlackIncomingMessage getStockQuotes(List<String> tokens) {
		// Remove duplicate symbols
		List<String> symbols = new ArrayList<>(new LinkedHashSet<>(tokens));

		Optional<StockQuotes> stockQuotes = googleFinanceClient.getStockQuotes(symbols);
		log.debug("Returned stock quote {}", stockQuotes);
		SlackIncomingMessage message = new SlackIncomingMessage();
		if (stockQuotes.isPresent()) {
			message = conversionService.convert(stockQuotes.get(), SlackIncomingMessage.class);
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

	private SlackIncomingMessage chart(List<String> tokens) {
		SlackIncomingMessage slackIncomingMessage = new SlackIncomingMessage();
		if (!tokens.isEmpty()) {
			// only build the URL for the first symbol given
			slackIncomingMessage.setText(MessageFormat.format("<http://chart.finance.yahoo.com/t?s={0}&lang=en-US&region=US&width=300&height=180&cb={1,number,#}>", tokens.get(0), System.currentTimeMillis()));
		}
		return slackIncomingMessage;
	}
}
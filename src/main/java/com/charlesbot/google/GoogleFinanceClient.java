package com.charlesbot.google;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.charlesbot.model.StockQuotes;
import com.google.common.base.Joiner;

@Named
public class GoogleFinanceClient {

	private static final Logger log = LoggerFactory.getLogger(GoogleFinanceClient.class);
	
	@Inject
	private RestTemplate restTemplate;
	
	public Optional<StockQuotes> getStockQuotes(List<String> symbols) {
		log.debug("Building Request from {}", symbols);
		String symbolsString = Joiner.on(',').join(symbols);
		Optional<StockQuotes> stockQuotes = null;
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("symbols", symbolsString);
		StockQuotes quotes = restTemplate.getForObject("http://www.google.com/finance/info?infotype=infoquoteall&q={symbols}", StockQuotes.class, variables);
		log.debug("Completed Request {}", stockQuotes);
		return Optional.ofNullable(quotes);
	}
}

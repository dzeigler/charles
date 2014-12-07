package com.charlesbot.yahoo;

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
public class YahooFinanceClient {

	private static final Logger log = LoggerFactory.getLogger(YahooStockQuoteConverter.class);
	
	@Inject
	private RestTemplate restTemplate;
	
	public Optional<StockQuotes> getStockQuotes(List<String> symbols) {
		log.debug("Building Request from {}", symbols);
		String symbolsString = Joiner.on(' ').join(symbols);
		Optional<StockQuotes> stockQuotes = null;
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("symbols", symbolsString);
		variables.put("fields", "c1p2h0g0m3l1s0n0");
		StockQuotes quotes = restTemplate.getForObject("http://finance.yahoo.com/d/quotes.csv?s={symbols}&f={fields}", StockQuotes.class, variables);
		log.debug("Completed Request {}", stockQuotes);
		return Optional.ofNullable(quotes);
	}
}

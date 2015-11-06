package com.charlesbot.google;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.charlesbot.model.StockQuotes;
import com.google.common.base.Joiner;

@Named
public class GoogleFinanceClient {

	private static final Logger log = LoggerFactory.getLogger(GoogleFinanceClient.class);
	private static final String QUOTE_URL = "http://www.google.com/finance/info?infotype=infoquoteall&q={symbols}";
	@Inject
	private RestTemplate restTemplate;
	@Inject
	private AsyncRestTemplate asyncRestTemplate;

	public Optional<StockQuotes> getStockQuotes(List<String> symbols) {
		log.debug("Building Request from {}", symbols);
		String symbolsString = Joiner.on(',').join(symbols);
		Optional<StockQuotes> stockQuotes = null;
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("symbols", symbolsString);
		ResponseEntity<StockQuotes> quotes = restTemplate.getForEntity(QUOTE_URL, StockQuotes.class, variables);
		log.debug("Completed Request {}", stockQuotes);
		return Optional.ofNullable(quotes.getBody());
	}

	public void getAsyncStockQuotes(List<String> symbols, ListenableFutureCallback<ResponseEntity<StockQuotes>> callback) {
		log.debug("Building Request from {}", symbols);
		String symbolsString = Joiner.on(',').join(symbols);
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("symbols", symbolsString);
		ListenableFuture<ResponseEntity<StockQuotes>> futureEntity = asyncRestTemplate.getForEntity(QUOTE_URL, StockQuotes.class, variables);

		futureEntity.addCallback(callback);

	}
}

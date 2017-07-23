package com.charlesbot.coinbase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.charlesbot.model.CurrencyExchangeRates;

public class CoinBaseClient {

	private static final Logger log = LoggerFactory.getLogger(CoinBaseClient.class);
	private static final String EXCHANGE_RATES_URL = "https://api.coinbase.com/v2/exchange-rates?currency={currency}";
	private RestTemplate restTemplate;

	public CoinBaseClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Optional<CurrencyExchangeRates> getExchangeRates(String baseCurrency) {
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("currency", baseCurrency);
		log.debug("Building Request from {}", variables);
		try {
			ResponseEntity<CurrencyExchangeRates> exchangeRates = restTemplate.getForEntity(EXCHANGE_RATES_URL, CurrencyExchangeRates.class, variables);
			log.debug("Completed Request {}", exchangeRates);
			return Optional.ofNullable(exchangeRates.getBody());
		} catch (RestClientException e) {
			log.error("Error occurred while processing request", e);
		}
		return Optional.empty();
	}

}

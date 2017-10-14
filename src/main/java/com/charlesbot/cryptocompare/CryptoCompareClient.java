package com.charlesbot.cryptocompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.charlesbot.coinbase.CoinBaseClient;
import com.charlesbot.model.CurrencyQuote;

public class CryptoCompareClient {

	private static final Logger log = LoggerFactory.getLogger(CoinBaseClient.class);
	private static final String PRICE_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms={fromCurrencies}&tsyms={toCurrency}";
	private RestTemplate restTemplate;

	public CryptoCompareClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<CurrencyQuote> getPrices(List<String> fromCurrencies, String toCurrency) {
		Map<String, String> variables = new HashMap<String, String>();
		String fromCurrenciesString = fromCurrencies.stream().collect(Collectors.joining(","));
		variables.put("fromCurrencies", fromCurrenciesString);
		variables.put("toCurrency", toCurrency);
		log.debug("Building Request from {}", variables);
		try {
			ResponseEntity<PriceRequestResult> exchangeRates = restTemplate.getForEntity(PRICE_URL, PriceRequestResult.class, variables);
			log.debug("Completed Request {}", exchangeRates);
			
			if (exchangeRates.getBody() != null && exchangeRates.getBody().display != null) {
				List<CurrencyQuote> prices = convertToCurrencyQuote(exchangeRates.getBody());
				return prices;
			}
		} catch (RestClientException e) {
			log.error("Error occurred while processing request", e);
		}
		return new ArrayList<>();
	}

	private List<CurrencyQuote> convertToCurrencyQuote(PriceRequestResult result) {
		List<CurrencyQuote> prices = new ArrayList<>();
		
		for (PriceInfo priceInfo : result.display.getPrices()) {
			CurrencyQuote currencyQuote = new CurrencyQuote();
			prices.add(currencyQuote);
			currencyQuote.setFromCurrency(priceInfo.fromCode);
			currencyQuote.setToCurrency(priceInfo.toCode);
			currencyQuote.setChange24Hour(priceInfo.change24Hour);
			currencyQuote.setChangePercent24Hour(priceInfo.changePercent24Hour);
			currencyQuote.setFromSymbol(priceInfo.fromSymbol);
			currencyQuote.setHigh24Hour(priceInfo.high24Hour);
			currencyQuote.setLastUpdate(priceInfo.lastUpdate);
			currencyQuote.setLow24Hour(priceInfo.low24Hour);
			currencyQuote.setMarket(priceInfo.market);
			currencyQuote.setMarketCap(priceInfo.marketCap);
			currencyQuote.setOpen24Hour(priceInfo.open24Hour);
			currencyQuote.setPrice(priceInfo.price);
			currencyQuote.setSupply(priceInfo.supply);
			currencyQuote.setToSymbol(priceInfo.toSymbol);
		}
		return prices;
	}

}
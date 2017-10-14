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
import com.charlesbot.model.CurrencyPrice;

public class CryptoCompareClient {

	private static final Logger log = LoggerFactory.getLogger(CoinBaseClient.class);
	private static final String PRICE_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms={fromCurrencies}&tsyms={toCurrency}";
	private RestTemplate restTemplate;

	public CryptoCompareClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<CurrencyPrice> getPrices(List<String> fromCurrencies, String toCurrency) {
		Map<String, String> variables = new HashMap<String, String>();
		String fromCurrenciesString = fromCurrencies.stream().collect(Collectors.joining(","));
		variables.put("fromCurrencies", fromCurrenciesString);
		variables.put("toCurrency", toCurrency);
		log.debug("Building Request from {}", variables);
		try {
			ResponseEntity<PriceRequestResult> exchangeRates = restTemplate.getForEntity(PRICE_URL, PriceRequestResult.class, variables);
			log.debug("Completed Request {}", exchangeRates);
			
			if (exchangeRates.getBody() != null && exchangeRates.getBody().display != null) {
				List<CurrencyPrice> prices = convertToCurrencyPrice(exchangeRates.getBody());
				return prices;
			}
		} catch (RestClientException e) {
			log.error("Error occurred while processing request", e);
		}
		return new ArrayList<>();
	}

	private List<CurrencyPrice> convertToCurrencyPrice(PriceRequestResult result) {
		List<CurrencyPrice> prices = new ArrayList<>();
		
		for (PriceInfo priceInfo : result.display.getPrices()) {
			CurrencyPrice currencyPrice = new CurrencyPrice();
			prices.add(currencyPrice);
			currencyPrice.setFromCurrency(priceInfo.fromCode);
				currencyPrice.setToCurrency(priceInfo.toCode);
				currencyPrice.setChange24Hour(priceInfo.change24Hour);
				currencyPrice.setChangePercent24Hour(priceInfo.changePercent24Hour);
				currencyPrice.setFromSymbol(priceInfo.fromSymbol);
				currencyPrice.setHigh24Hour(priceInfo.high24Hour);
				currencyPrice.setLastUpdate(priceInfo.lastUpdate);
				currencyPrice.setLow24Hour(priceInfo.low24Hour);
				currencyPrice.setMarket(priceInfo.market);
				currencyPrice.setMarketCap(priceInfo.marketCap);
				currencyPrice.setOpen24Hour(priceInfo.open24Hour);
				currencyPrice.setPrice(priceInfo.price);
				currencyPrice.setSupply(priceInfo.supply);
				currencyPrice.setToSymbol(priceInfo.toSymbol);
		}
		return prices;
	}

}
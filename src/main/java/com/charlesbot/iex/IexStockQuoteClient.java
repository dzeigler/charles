package com.charlesbot.iex;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.google.common.base.Joiner;

@Named
public class IexStockQuoteClient {

	private static final Logger log = LoggerFactory.getLogger(IexStockQuoteConverter.class);
	private static final String QUOTE_URL = "https://api.iextrading.com/1.0/stock/market/batch?symbols={symbols}&types=quote,stats&displayPercent=true";
	private RestTemplate restTemplate;

	public IexStockQuoteClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Optional<StockQuotes> getStockQuotes(List<String> symbols) {
		log.debug("Building Request from {}", symbols);
		String symbolsString = Joiner.on(',').join(symbols);
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("symbols", symbolsString);
		try {
			ResponseEntity<BatchResponse> response = restTemplate.getForEntity(QUOTE_URL, BatchResponse.class, variables);
			log.debug("Completed Request {}", response);
			StockQuotes quotes = convertToStockQuote(response.getBody());
			return Optional.ofNullable(quotes);
		} catch (RestClientException e) {
			log.error("Error occurred while processing request", e);
		}
		return Optional.empty();
	}

	private StockQuotes convertToStockQuote(BatchResponse response) {
		StockQuotes quotes = new StockQuotes();
		
		for (Stock priceInfo : response.getStocks()) {
			StockQuote stockQuote = new StockQuote();
			quotes.get().add(stockQuote);
			if (priceInfo.quote != null) {
				Quote quote = priceInfo.quote;
				if (quote.change != null) {
					stockQuote.setChange(quote.change.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.changePercent != null) {
					stockQuote.setChangeInPercent(quote.changePercent.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.week52High != null) {
					stockQuote.setFiftyTwoWeekHigh(quote.week52High.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.week52High != null) {
					stockQuote.setFiftyTwoWeekLow(quote.week52Low.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.marketCap != null) {
					String formattedMarketCap = quote.marketCap.toString();
					if (quote.marketCap.compareTo(new BigDecimal(1000000000)) > 0) {
						formattedMarketCap = String.format("%.2fB", quote.marketCap.divide(new BigDecimal(1000000000)));
					} else if (quote.marketCap.compareTo(new BigDecimal(1000000)) > 0) {
						formattedMarketCap = String.format("%.2fM", quote.marketCap.divide(new BigDecimal(1000000)));
					}
					stockQuote.setMarketCap(formattedMarketCap);
				}
				if (quote.companyName != null) {
					stockQuote.setName(quote.companyName.toString());
				}
				if (quote.peRatio != null) {
					stockQuote.setPe(quote.peRatio.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.latestPrice != null) {
					stockQuote.setPrice(quote.latestPrice.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.symbol != null) {
					stockQuote.setSymbol(quote.symbol.toString());
				}
			}
			if (priceInfo.stats != null) {
				Stats stats = priceInfo.stats;
				if (stats.latestEps != null) {
					stockQuote.setEps(stats.latestEps.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (stats.dividendYield != null) {
					stockQuote.setYield(stats.dividendYield.setScale(2, RoundingMode.HALF_UP).toString());
				}
				
			}
		}
		return quotes;
	}
	
}

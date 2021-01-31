package com.charlesbot.iex;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.google.common.base.Joiner;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class IexStockQuoteClient {

	private static final Logger log = LoggerFactory.getLogger(IexStockQuoteConverter.class);
	private static final String QUOTE_URL = "https://cloud.iexapis.com/stable/stock/market/batch?symbols={symbols}&types={types}&displayPercent=true&token={iexApiToken}";
	private RestTemplate restTemplate;

	private String iexApiToken;

	public IexStockQuoteClient(RestTemplate restTemplate, String iexApiToken) {
		this.restTemplate = restTemplate;
		this.iexApiToken = iexApiToken;
	}

	private Optional<StockQuotes> getStockQuotes(List<String> symbols, String types) {
		log.debug("Building Request from {}", symbols);
		String symbolsString = Joiner.on(',').join(symbols);
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("symbols", symbolsString);
		variables.put("iexApiToken", iexApiToken);
		variables.put("types", types);
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
	
	
	public Optional<StockQuotes> getStockQuotes(List<String> symbols) {
		return getStockQuotes(symbols, "quote");
	}
	
	public Optional<StockQuotes> getStockQuotesAndStats(List<String> symbols) {
		return getStockQuotes(symbols, "quote,stats");
	}

	private StockQuotes convertToStockQuote(BatchResponse response) {
		StockQuotes quotes = new StockQuotes();
		
		for (Stock priceInfo : response.getStocks()) {
			StockQuote stockQuote = new StockQuote();
			if (priceInfo.quote != null && priceInfo.quote.symbol != null) {
				quotes.get().add(stockQuote);
			} else {
				continue;
			}
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
				if (quote.week52Low != null) {
					stockQuote.setFiftyTwoWeekLow(quote.week52Low.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.marketCap != null) {
					String formattedMarketCap = quote.marketCap.toString();
					if (quote.marketCap.compareTo(new BigDecimal(1_000_000_000_000L)) > 0) {
						formattedMarketCap = String.format("%.2fT", quote.marketCap.divide(new BigDecimal(1_000_000_000_000L)));
					} else if (quote.marketCap.compareTo(new BigDecimal(1_000_000_000)) > 0) {
						formattedMarketCap = String.format("%.2fB", quote.marketCap.divide(new BigDecimal(1_000_000_000)));
					} else if (quote.marketCap.compareTo(new BigDecimal(1_000_000)) > 0) {
						formattedMarketCap = String.format("%.2fM", quote.marketCap.divide(new BigDecimal(1_000_000)));
					}
					stockQuote.setMarketCap(formattedMarketCap);
				}
				if (quote.companyName != null) {
					stockQuote.setName(quote.companyName);
				}
				if (quote.peRatio != null) {
					stockQuote.setPe(quote.peRatio.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.latestPrice != null) {
					stockQuote.setPrice(quote.latestPrice.setScale(2, RoundingMode.HALF_UP).toString());
				}
				if (quote.symbol != null) {
					stockQuote.setSymbol(quote.symbol);
				}
				if (quote.iexRealtimePrice != null && quote.iexRealtimePrice.compareTo(quote.latestPrice) != 0
						&& BigDecimal.ZERO.compareTo(quote.iexRealtimePrice) != 0 
						&& quote.iexLastUpdated != null && quote.latestUpdate != null 
						&& quote.iexLastUpdated > quote.latestUpdate) {
					stockQuote.setExtendedHoursPrice(quote.iexRealtimePrice.setScale(2, RoundingMode.HALF_UP).toString());
					BigDecimal change = quote.iexRealtimePrice.subtract(quote.latestPrice);
					BigDecimal changePercent = change.multiply(new BigDecimal(100)).divide(quote.latestPrice, 2, RoundingMode.HALF_UP);
					if (quote.changePercent != null) {
						stockQuote.setExtendedHoursChangeInPercent(changePercent.toString());
					}
					if (quote.change != null) {
						stockQuote.setExtendedHoursChange(change.toString());
					}
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

package com.charlesbot.yahoo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yql4j.ResultFormat;
import org.yql4j.YqlClient;
import org.yql4j.YqlClients;
import org.yql4j.YqlQuery;
import org.yql4j.YqlQueryBuilder;
import org.yql4j.YqlResult;
import org.yql4j.types.QueryResultType;

import com.charlesbot.model.StockQuotes;
import com.fasterxml.jackson.core.type.TypeReference;

@Named
public class YahooFinanceClient {

	private static final Logger log = LoggerFactory.getLogger(YahooStockQuoteConverter.class);

	public Optional<StockQuotes> getStockQuotes(List<String> symbols) {
		log.debug("Building Request from {}", symbols);
		String symbolsString = symbols.stream().map((s) -> "\"" + s + "\"").collect(Collectors.joining(", "));
		Optional<StockQuotes> quotes = null;
		StockQuotes stockQuotesResult = null;
		// https://query.yahooapis.com/v1/public/yql?q=select%20%2a%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28%22nflx%22,%22aapl%22,%22shop%22%29&env=store://datatables.org/alltableswithkeys
		YqlClient client = YqlClients.createDefault();
		YqlQuery query = YqlQueryBuilder
				.fromQueryString("select * from yahoo.finance.quotes where symbol in (@symbols)")
				.withEnvironment("store://datatables.org/alltableswithkeys")
				.withFormat(ResultFormat.JSON)
				.withVariable("symbols", symbolsString).build();
		try {
			YqlResult result = client.query(query);
			stockQuotesResult = result.getContentAsMappedObject(new TypeReference<QueryResultType<StockQuotes>>() {}).getResults();
			log.debug("Completed Request {}", quotes);
		} catch (Exception e) {
			log.error("Error occurred while processing request", e);
		}
		return Optional.ofNullable(stockQuotesResult);
	}

}

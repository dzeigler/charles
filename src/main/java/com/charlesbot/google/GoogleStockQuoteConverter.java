package com.charlesbot.google;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GoogleStockQuoteConverter extends AbstractHttpMessageConverter<StockQuotes> {

	private static final Logger log = LoggerFactory.getLogger(GoogleStockQuoteConverter.class);
	
	public GoogleStockQuoteConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		log.trace("supports {} returns {}", clazz, StockQuote.class.equals(clazz));
		return StockQuotes.class.equals(clazz);
	}

	@Override
	protected StockQuotes readInternal(Class<? extends StockQuotes> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		ObjectMapper mapper = new ObjectMapper();
		StockQuotes stockQuotes = new StockQuotes();
		try {
			String body = null;
			try (java.util.Scanner s = new java.util.Scanner(inputMessage.getBody())) {
				body = s.useDelimiter("\\A").hasNext() ? s.next() : ""; 
			}
			body = body.substring(3);
			log.info("body='{}'", body);
			
			List<StockQuote> quotes = mapper.readValue(body, new TypeReference<List<StockQuote>>(){});
			stockQuotes.get().addAll(quotes);
		} catch (Exception e) {
			log.error("Failure while parsing the json", e);
		}
		return stockQuotes;
	}

	@Override
	protected void writeInternal(StockQuotes t, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {

	}

}

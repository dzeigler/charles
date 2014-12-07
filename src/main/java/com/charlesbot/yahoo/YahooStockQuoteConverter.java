package com.charlesbot.yahoo;

import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

public class YahooStockQuoteConverter extends AbstractHttpMessageConverter<StockQuotes> {

	private static final Logger log = LoggerFactory.getLogger(YahooStockQuoteConverter.class);
	
	public YahooStockQuoteConverter() {
		super(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_FORM_URLENCODED);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		log.trace("supports {} returns {}", clazz, StockQuote.class.equals(clazz));
		return StockQuotes.class.equals(clazz);
	}

	@Override
	protected StockQuotes readInternal(Class<? extends StockQuotes> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		ICsvBeanReader beanReader = null;
		StockQuotes stockQuotes = new StockQuotes();
		try {
			beanReader = new CsvBeanReader(new InputStreamReader(inputMessage.getBody()), CsvPreference.STANDARD_PREFERENCE);
			StockQuote stockQuote = null;
			while ((stockQuote = beanReader.read(StockQuote.class, StockQuote.getHeader())) != null) {
				stockQuotes.get().add(stockQuote);
			}
		} catch (Exception e) {
			log.error("Failure while parsing the CSV", e);
		} finally {
			if (beanReader != null) {
				beanReader.close();
			}
		}
		return stockQuotes;
	}

	@Override
	protected void writeInternal(StockQuotes t, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {

	}

}

package com.charlesbot.coinbase;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.charlesbot.model.CurrencyExchangeRates;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CoinBaseCurrencyExchangeRateConverter extends AbstractHttpMessageConverter<CurrencyExchangeRates> {

	private static final Logger log = LoggerFactory.getLogger(CoinBaseCurrencyExchangeRateConverter.class);
	
	public CoinBaseCurrencyExchangeRateConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		log.trace("supports {} returns {}", clazz, CurrencyExchangeRates.class.equals(clazz));
		return CurrencyExchangeRates.class.equals(clazz);
	}

	@Override
	protected CurrencyExchangeRates readInternal(Class<? extends CurrencyExchangeRates> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		CurrencyExchangeRates currencyExchangeRates = new CurrencyExchangeRates();
		try {
			String body = null;
			try (java.util.Scanner s = new java.util.Scanner(inputMessage.getBody())) {
				body = s.useDelimiter("\\A").hasNext() ? s.next() : ""; 
			}
			log.info("body='{}'", body);
			
			currencyExchangeRates = mapper.readValue(body, new TypeReference<CurrencyExchangeRates>(){});
		} catch (Exception e) {
			log.error("Failure while parsing the json", e);
		}
		return currencyExchangeRates;
	}

	@Override
	protected void writeInternal(CurrencyExchangeRates t, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {

	}

}

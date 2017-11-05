package com.charlesbot.cli;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.charlesbot.slack.PercentRanges;
import com.charlesbot.slack.QuoteCommandLineOptionsToStrings;

public class QuoteCommandLineOptionsToStringsTest {

	@Mock
	private IexStockQuoteClient client;
	
	PercentRanges percentRanges = new PercentRanges();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	public void mockReturnWhatsPassedIn() {
		when(client.getStockQuotes(Mockito.anyListOf(String.class))).thenAnswer(new Answer<Optional<StockQuotes>>() {
			@SuppressWarnings("unchecked")
			@Override
			public Optional<StockQuotes> answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				List<String> symbols = (List<String>) args[0];
				StockQuotes quotes = new StockQuotes();
				for (String symbol : symbols) {
					StockQuote quote = new StockQuote();
					quote.setSymbol(symbol);
					quotes.get().add(quote);	
				}
				return Optional.of(quotes);
			}
		});
	}
	
	@Test
	public void quoteWithoutTickerSymbol() {
		QuoteCommandLineOptionsToStrings converter = new QuoteCommandLineOptionsToStrings(client, percentRanges);

		mockReturnWhatsPassedIn();
		QuoteCommandLineOptions options = new QuoteCommandLineOptions();
		options.forceHelp();

		List<String> output = converter.convert(options);

		assertThat(output, is(notNullValue()));
	}

	@Test
	public void quoteWithTickerSymbol() {
		QuoteCommandLineOptionsToStrings converter = new QuoteCommandLineOptionsToStrings(client, percentRanges);
		mockReturnWhatsPassedIn();
		QuoteCommandLineOptions options = new QuoteCommandLineOptions();
		options.tickerSymbols.add("tsla");

		List<String> output = converter.convert(options);

		assertThat(output, is(notNullValue()));
	}
}

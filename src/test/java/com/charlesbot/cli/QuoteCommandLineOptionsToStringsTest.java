package com.charlesbot.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.charlesbot.slack.PercentRanges;
import com.charlesbot.slack.QuoteCommandLineOptionsToStrings;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public class QuoteCommandLineOptionsToStringsTest {

	@Mock
	private IexStockQuoteClient client;
	
	PercentRanges percentRanges = new PercentRanges();

	public void mockReturnWhatsPassedIn() {
		when(client.getStockQuotes(Mockito.anyList())).thenAnswer((Answer<Optional<StockQuotes>>) invocation -> {
			Object[] args = invocation.getArguments();
			List<String> symbols = (List<String>) args[0];
			StockQuotes quotes = new StockQuotes();
			for (String symbol : symbols) {
				StockQuote quote = new StockQuote();
				quote.setSymbol(symbol);
				quotes.get().add(quote);
			}
			return Optional.of(quotes);
		});
	}
	
	@Test
	public void quoteWithoutTickerSymbol() {
		QuoteCommandLineOptionsToStrings converter = new QuoteCommandLineOptionsToStrings(client, percentRanges);

		QuoteCommandLineOptions options = new QuoteCommandLineOptions();
		options.forceHelp();

		List<String> output = converter.convert(options);

		assertThat(output).isNotNull();
	}

	@Test
	public void quoteWithTickerSymbol() {
		QuoteCommandLineOptionsToStrings converter = new QuoteCommandLineOptionsToStrings(client, percentRanges);
		mockReturnWhatsPassedIn();
		QuoteCommandLineOptions options = new QuoteCommandLineOptions();
		options.tickerSymbols.add("tsla");

		List<String> output = converter.convert(options);

		assertThat(output).isNotNull();
	}
}

package com.charlesbot;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.coinbase.CoinBaseClient;
import com.charlesbot.coinbase.CoinBaseCurrencyExchangeRateConverter;
import com.charlesbot.cryptocompare.CryptoCompareClient;
import com.charlesbot.google.GoogleStockQuoteConverter;
import com.charlesbot.model.WatchListRepository;
import com.charlesbot.slack.AddToListCommandLineOptionsToStrings;
import com.charlesbot.slack.ChartCommandLineOptionsToStrings;
import com.charlesbot.slack.CurrencyExchangeRateCommandLineOptionsToStrings;
import com.charlesbot.slack.CurrencyPriceCommandLineOptionsToStrings;
import com.charlesbot.slack.HelpCommandLineOptionsToStrings;
import com.charlesbot.slack.ListCommandLineOptionsToStrings;
import com.charlesbot.slack.ListQuoteCommandLineOptionsToStrings;
import com.charlesbot.slack.ListStatsCommandLineOptionsToStrings;
import com.charlesbot.slack.PercentRanges;
import com.charlesbot.slack.QuoteCommandLineOptionsToStrings;
import com.charlesbot.slack.RemoveFromListCommandLineOptionsToStrings;
import com.charlesbot.slack.StatsCommandLineOptionsToStrings;
import com.charlesbot.slack.StringToPortfolioQuoteMessage;
import com.charlesbot.yahoo.YahooFinanceClient;
import com.charlesbot.yahoo.YahooStockQuoteConverter;

@SpringBootApplication
@Configuration
public class Application extends WebMvcConfigurerAdapter {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer ppc() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public HttpMessageConverters customConverters() {
		HttpMessageConverter<?> yahooStockQuoteConverter = new YahooStockQuoteConverter();
		HttpMessageConverter<?> googleStockQuoteConverter = new GoogleStockQuoteConverter();
		HttpMessageConverter<?> coinBaseCurrencyExchangeRateConverter = new CoinBaseCurrencyExchangeRateConverter();
		return new HttpMessageConverters(yahooStockQuoteConverter, googleStockQuoteConverter, coinBaseCurrencyExchangeRateConverter);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(customConverters().getConverters());
	}

	@Bean
	public AsyncRestTemplate asyncRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setTaskExecutor(new SimpleAsyncTaskExecutor());
		AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(factory, restTemplate());
		return asyncRestTemplate;
	}

	@Bean
	public PercentRanges percentRanges() {
		return new PercentRanges();
	}
	
	@Bean
	public ConversionServiceFactoryBean conversionService(YahooFinanceClient yahooFinanceClient, CoinBaseClient coinBaseClient, 
			CryptoCompareClient cryptoCompareClient, WatchListRepository watchListRepository, PercentRanges percentRanges) {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		Set<Converter<?, ?>> converters = new HashSet<>();
		
		QuoteCommandLineOptionsToStrings quoteCommandLineOptionsToStrings = new QuoteCommandLineOptionsToStrings(yahooFinanceClient, percentRanges);
		converters.add(quoteCommandLineOptionsToStrings);
		converters.add(new StatsCommandLineOptionsToStrings(yahooFinanceClient));
		converters.add(new ChartCommandLineOptionsToStrings());
		converters.add(new AddToListCommandLineOptionsToStrings(watchListRepository));
		converters.add(new RemoveFromListCommandLineOptionsToStrings(watchListRepository));
		converters.add(new ListCommandLineOptionsToStrings(watchListRepository));
		converters.add(new ListQuoteCommandLineOptionsToStrings(watchListRepository, quoteCommandLineOptionsToStrings));
		converters.add(new HelpCommandLineOptionsToStrings());
		converters.add(new StringToPortfolioQuoteMessage(yahooFinanceClient));
		converters.add(new ListStatsCommandLineOptionsToStrings(watchListRepository, yahooFinanceClient));
		converters.add(new CurrencyExchangeRateCommandLineOptionsToStrings(coinBaseClient));
		converters.add(new CurrencyPriceCommandLineOptionsToStrings(cryptoCompareClient, percentRanges));
		conversionServiceFactoryBean.setConverters(converters);
		return conversionServiceFactoryBean;
	}
	
	@Bean
    public FormattingConversionService formattingConversionService() {
        FormattingConversionService conversionService = new FormattingConversionServiceFactoryBean().getObject();
        addFormatters(conversionService);
        return conversionService;
    }

	@Bean
	public YahooFinanceClient yahooFinanceClient() {
		return new YahooFinanceClient();
	}
	
	@Bean
	public CoinBaseClient coinBaseClient(RestTemplate restTemplate) {
		return new CoinBaseClient(restTemplate);
	}
	
	@Bean
	public CryptoCompareClient cryptoCompareClient(RestTemplate restTemplate) {
		return new CryptoCompareClient(restTemplate);
	}

	@Bean
	public CommandLineProcessor commandLineProcessor() {
		return new CommandLineProcessor();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

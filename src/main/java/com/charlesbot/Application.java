package com.charlesbot;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
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
import com.charlesbot.google.GoogleFinanceClient;
import com.charlesbot.google.GoogleStockQuoteConverter;
import com.charlesbot.model.WatchListRepository;
import com.charlesbot.slack.AddToListCommandLineOptionsToStrings;
import com.charlesbot.slack.ChartCommandLineOptionsToStrings;
import com.charlesbot.slack.HelpCommandLineOptionsToStrings;
import com.charlesbot.slack.ListCommandLineOptionsToStrings;
import com.charlesbot.slack.ListQuoteCommandLineOptionsToStrings;
import com.charlesbot.slack.QuoteCommandLineOptionsToStrings;
import com.charlesbot.slack.RemoveFromListCommandLineOptionsToStrings;
import com.charlesbot.slack.StatsCommandLineOptionsToStrings;
import com.charlesbot.slack.StringToPortfolioQuoteMessage;
import com.charlesbot.yahoo.YahooStockQuoteConverter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends WebMvcConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Bean
	public HttpMessageConverters customConverters() {
		HttpMessageConverter<?> yahooStockQuoteConverter = new YahooStockQuoteConverter();
		HttpMessageConverter<?> googleStockQuoteConverter = new GoogleStockQuoteConverter();
		return new HttpMessageConverters(yahooStockQuoteConverter, googleStockQuoteConverter);
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
	public ConversionServiceFactoryBean conversionService(GoogleFinanceClient googleFinanceClient, WatchListRepository watchListRepository, AddToListCommandLineOptionsToStrings addToListCommandLineOptionsToString, RemoveFromListCommandLineOptionsToStrings removeFromListCommandLineOptionsToString
			, ListCommandLineOptionsToStrings listCommandLineOptionsToString
			, ChartCommandLineOptionsToStrings chartCommandLineOptionsToString
			, StatsCommandLineOptionsToStrings statsCommandLineOptionsToString
			, QuoteCommandLineOptionsToStrings quoteCommandLineOptionsToString
			, StringToPortfolioQuoteMessage stringToPortfolioQuoteMessage
			, ListQuoteCommandLineOptionsToStrings listQuoteCommandLineOptionsToString
			, HelpCommandLineOptionsToStrings helpCommandLineOptionsToString) {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		Set<Converter<?, ?>> converters = new HashSet<>();
		converters.add(quoteCommandLineOptionsToString);
		converters.add(statsCommandLineOptionsToString);
		converters.add(chartCommandLineOptionsToString);
		converters.add(addToListCommandLineOptionsToString);
		converters.add(removeFromListCommandLineOptionsToString);
		converters.add(listCommandLineOptionsToString);
		converters.add(listQuoteCommandLineOptionsToString);
		converters.add(helpCommandLineOptionsToString);
		converters.add(stringToPortfolioQuoteMessage);
		conversionServiceFactoryBean.setConverters(converters);
		return conversionServiceFactoryBean;
	}
	
	@Bean
    public FormattingConversionService conversionService() {
        FormattingConversionService conversionService = new FormattingConversionServiceFactoryBean().getObject();
        addFormatters(conversionService);
        return conversionService;
    }

	@Bean
	public GoogleFinanceClient googleFinanceClient(RestTemplate restTemplate, AsyncRestTemplate asyncRestTemplate) {
		return new GoogleFinanceClient(restTemplate, asyncRestTemplate);
	}

	@Bean
	public CommandLineProcessor commandLineProcessor() {
		return new CommandLineProcessor();
	}

	public static void main(String[] args) {
		logger.debug("Starting application");
		SpringApplication.run(Application.class, args);
	}
}

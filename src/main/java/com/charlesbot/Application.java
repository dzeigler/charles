package com.charlesbot;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.charlesbot.google.GoogleStockQuoteConverter;
import com.charlesbot.slack.StockQuotesToQuoteMessage;
import com.charlesbot.slack.StockQuotesToQuoteMessage2;
import com.charlesbot.slack.StockQuotesToStatsMessage;
import com.charlesbot.slack.StringToChartMessage;
import com.charlesbot.yahoo.YahooStockQuoteConverter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends WebMvcConfigurerAdapter {

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
	public ConversionServiceFactoryBean conversionService() {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		Set<Converter<?, ?>> converters = new HashSet<>();
		converters.add(new StockQuotesToQuoteMessage());
		converters.add(new StockQuotesToQuoteMessage2());
		converters.add(new StockQuotesToStatsMessage());
		converters.add(new StringToChartMessage());
		conversionServiceFactoryBean.setConverters(converters);
		return conversionServiceFactoryBean;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

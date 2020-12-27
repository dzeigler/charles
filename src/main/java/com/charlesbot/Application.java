package com.charlesbot;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cryptocompare.CryptoCompareClient;
import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.iex.IexStockQuoteConverter;
import com.charlesbot.model.UserRepository;
import com.charlesbot.model.WatchListRepository;
import com.charlesbot.service.PositionService;
import com.charlesbot.slack.AddToListCommandLineOptionsToStrings;
import com.charlesbot.slack.ChartCommandLineOptionsToStrings;
import com.charlesbot.slack.CurrencyChartCommandLineOptionsToStrings;
import com.charlesbot.slack.CurrencyQuoteCommandLineOptionsToStrings;
import com.charlesbot.slack.HelpCommandLineOptionsToStrings;
import com.charlesbot.slack.ListCommandLineOptionsToStrings;
import com.charlesbot.slack.ListQuoteCommandLineOptionsToStrings;
import com.charlesbot.slack.ListStatsCommandLineOptionsToStrings;
import com.charlesbot.slack.PercentRanges;
import com.charlesbot.slack.QuoteCommandLineOptionsToStrings;
import com.charlesbot.slack.RemoveFromListCommandLineOptionsToStrings;
import com.charlesbot.slack.StatsCommandLineOptionsToStrings;
import com.charlesbot.slack.StringToPortfolioQuoteMessage;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Configuration
public class Application implements WebMvcConfigurer {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer ppc() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Value("${IEX_API_TOKEN}")
	private String iexApiToken;
	
	@Bean
	public HttpMessageConverters customConverters() {
		HttpMessageConverter<?> iexStockQuoteConverter = new IexStockQuoteConverter();
		return new HttpMessageConverters(iexStockQuoteConverter);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(customConverters().getConverters());
	}

	@Bean
	public PercentRanges percentRanges() {
		return new PercentRanges();
	}
	
	@Bean
	public ConversionServiceFactoryBean conversionService(IexStockQuoteClient iexStockQuoteClient, 
			CryptoCompareClient cryptoCompareClient, WatchListRepository watchListRepository, UserRepository userRepository,
			PercentRanges percentRanges, PositionService positionService) {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		Set<Converter<?, ?>> converters = new HashSet<>();
		
		QuoteCommandLineOptionsToStrings quoteCommandLineOptionsToStrings = new QuoteCommandLineOptionsToStrings(iexStockQuoteClient, percentRanges);
		converters.add(quoteCommandLineOptionsToStrings);
		converters.add(new StatsCommandLineOptionsToStrings(iexStockQuoteClient));
		converters.add(new ChartCommandLineOptionsToStrings());
		converters.add(new AddToListCommandLineOptionsToStrings(watchListRepository));
		converters.add(new RemoveFromListCommandLineOptionsToStrings(watchListRepository));
		converters.add(new ListCommandLineOptionsToStrings(watchListRepository, userRepository));
		converters.add(new ListQuoteCommandLineOptionsToStrings(watchListRepository, quoteCommandLineOptionsToStrings, positionService));
		converters.add(new HelpCommandLineOptionsToStrings());
		converters.add(new StringToPortfolioQuoteMessage(iexStockQuoteClient));
		converters.add(new ListStatsCommandLineOptionsToStrings(watchListRepository, positionService, iexStockQuoteClient));
		converters.add(new CurrencyQuoteCommandLineOptionsToStrings(cryptoCompareClient, percentRanges));
		converters.add(new CurrencyChartCommandLineOptionsToStrings());
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
	public IexStockQuoteClient iexStockQuoteClient(RestTemplate restTemplate) {
		return new IexStockQuoteClient(restTemplate, iexApiToken);
	}
	
	@Bean
	public CryptoCompareClient cryptoCompareClient(RestTemplate restTemplate) {
		return new CryptoCompareClient(restTemplate);
	}

	@Bean
	public CommandLineProcessor commandLineProcessor(UserRepository userRepository) {
		return new CommandLineProcessor(userRepository);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

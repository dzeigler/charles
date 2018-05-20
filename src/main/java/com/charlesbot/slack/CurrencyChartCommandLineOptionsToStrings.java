package com.charlesbot.slack;

import java.text.MessageFormat;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.CurrencyChartCommandLineOptions;
import com.charlesbot.cli.CurrencyChartOption;

@Component
public class CurrencyChartCommandLineOptionsToStrings implements CommandConverter<CurrencyChartCommandLineOptions> {

	@Override
	public List<String> convert(CurrencyChartCommandLineOptions options) {
		String output = null;
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output = "```"+helpMessage+"```";
		} else {
			CurrencyChartOption option = CurrencyChartCommandLineOptions.SUPPORTED_TIME_SPANS.get(options.timeSpan);
			String fromCurrency = options.fromCurrency;
			String toCurrency = options.toCurrency;
			
			String url = MessageFormat.format("<https://cryptohistory.org/charts/{4}/{0}-{1}/{2}/png?cb={3,number,#}>", fromCurrency, toCurrency, option.getTimeSpan(), System.currentTimeMillis(), option.getTheme());
			
			output = url;
		}
		return Lists.newArrayList(output);
	}

}

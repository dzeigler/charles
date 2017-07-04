package com.charlesbot.slack;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

import com.charlesbot.cli.ChartCommandLineOptions;
import com.charlesbot.cli.CommandLineProcessor;

@Component
public class ChartCommandLineOptionsToStrings implements CommandConverter<ChartCommandLineOptions> {

	@Override
	public List<String> convert(ChartCommandLineOptions options) {
		String output = null;
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output = "```"+helpMessage+"```";
		} else {
			String timeSpan = ChartCommandLineOptions.SUPPORTED_TIME_SPANS.get(options.timeSpan);
			String compare = options.symbolsToCompare
					.stream()
					.filter(StringUtils::isNotBlank)	
					.collect(Collectors.joining(","));
			String symbol = options.tickerSymbol;
			
//			String url = MessageFormat.format("<http://chart.finance.yahoo.com/z?s={0}&t={1}&c={2}&q=l&z=l&p=s&a=v&cb={3,number,#}>", symbol, timeSpan, compare, System.currentTimeMillis());
			String url = MessageFormat.format("http://bigcharts.marketwatch.com/kaavio.Webhost/charts/big.chart?symb={0}&time={1}&comp={2}&cb={3,number,#}>", symbol, timeSpan, compare, System.currentTimeMillis());
			
			output = url;
		}
		return Lists.newArrayList(output);
	}

}

package com.charlesbot.slack;

import com.charlesbot.cli.ChartCommandLineOptions;
import com.charlesbot.cli.CommandLineProcessor;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

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
			String frequency = "1";
			if (timeSpan.equals("1") || timeSpan.equals("2") || timeSpan.equals("3") || timeSpan.equals("18")) {
				frequency = "6";
			}
			String compare = options.symbolsToCompare
					.stream()
					.filter(StringUtils::isNotBlank)	
					.collect(Collectors.joining(","));
			String symbol = options.tickerSymbol;
			
			String url = MessageFormat.format("<https://api.wsj.net/api/kaavio/charts/big.chart?symb={0}&time={1}&freq={2}&comp={3}&cb={4,number,#}>.gif", symbol, timeSpan, frequency, compare, System.currentTimeMillis());
			
			output = url;
		}
		return Lists.newArrayList(output);
	}

}

package com.charlesbot.slack;

import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.brsanthu.dataexporter.DataExporter;
import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.StringColumn;
import com.brsanthu.dataexporter.output.texttable.TextTableExporter;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.StatsCommandLineOptions;
import com.charlesbot.google.GoogleFinanceClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

@Component
public class StatsCommandLineOptionsToString implements Converter<StatsCommandLineOptions, String> {

	@Autowired
	private GoogleFinanceClient googleFinanceClient;
	
	public StatsCommandLineOptionsToString() {
	}
	
	@Override
	public String convert(StatsCommandLineOptions options) {
		StringBuilder sb = new StringBuilder();
		if (options.isHelp()) {
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			sb.append("```"+helpMessage+"```");
		} else {

			StockQuotes stockQuotes = googleFinanceClient.getStockQuotes(options.tickerSymbols).get();
			
			sb.append("```");
			
			StringWriter stringWriter = new StringWriter();
			DataExporter exporter = new TextTableExporter(stringWriter);
			exporter.addColumns(
					new StringColumn("Symbol",8, AlignType.TOP_LEFT),
					new StringColumn("Name",20, AlignType.TOP_LEFT),
					new StringColumn("Price",10, AlignType.TOP_RIGHT),
					new StringColumn("Mkt Cap",8, AlignType.TOP_RIGHT),
					new StringColumn("P/E",8, AlignType.TOP_RIGHT),
					new StringColumn("EPS",10, AlignType.TOP_RIGHT),
					new StringColumn("Day Low",10, AlignType.TOP_RIGHT),
					new StringColumn("Day High",10, AlignType.TOP_RIGHT),
					new StringColumn("52wk Low",10, AlignType.TOP_RIGHT),
					new StringColumn("52wk High",10, AlignType.TOP_RIGHT)
					);
			
			for (StockQuote quote : stockQuotes.get()) {
				exporter.addRow(quote.getSymbol(), quote.getName(), quote.getCurrentPrice(), quote.getMarketCap(), quote.getPe(), quote.getEps(), quote.getDayLow(), quote.getDayHigh(),
						quote.getFiftyTwoWeekLow(), quote.getFiftyTwoWeekHigh());
				
			}
			exporter.finishExporting();
			sb.append(stringWriter.toString());
			sb.append("```");
		}
		return sb.toString();
	}

}

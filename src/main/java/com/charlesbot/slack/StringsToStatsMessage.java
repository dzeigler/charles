package com.charlesbot.slack;

import java.io.StringWriter;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.brsanthu.dataexporter.DataExporter;
import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.StringColumn;
import com.brsanthu.dataexporter.output.texttable.TextTableExporter;
import com.charlesbot.google.GoogleFinanceClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

public class StringsToStatsMessage implements Converter<List<String>, StatsMessage> {

	private GoogleFinanceClient googleFinanceClient;
	
	public StringsToStatsMessage(GoogleFinanceClient googleFinanceClient) {
		this.googleFinanceClient = googleFinanceClient;
	}
	
	@Override
	public StatsMessage convert(List<String> tokens) {
		StatsMessage message = new StatsMessage();
		StockQuotes stockQuotes = googleFinanceClient.getStockQuotes(tokens).get();
		StringBuilder sb = new StringBuilder();
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
		message.setText(sb.toString());
		
		return message;
	}

}

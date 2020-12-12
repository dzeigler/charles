package com.charlesbot.slack;

import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.Row;
import com.brsanthu.dataexporter.model.StringColumn;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.StatsCommandLineOptions;
import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import java.util.ArrayList;
import java.util.List;

public class StatsCommandLineOptionsToStrings implements CommandConverter<StatsCommandLineOptions> {

	private IexStockQuoteClient iexStockQuoteClient;
	
	public StatsCommandLineOptionsToStrings(IexStockQuoteClient iexStockQuoteClient) {
		this.iexStockQuoteClient = iexStockQuoteClient;
	}
	
	@Override
	public List<String> convert(StatsCommandLineOptions options) {
		List<String> outputs = new ArrayList<>();
		
		if (options.isHelp()) {
			StringBuilder sb = new StringBuilder();
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			sb.append("```"+helpMessage+"```");
			outputs.add(sb.toString());
		} else {

			StockQuotes stockQuotes = iexStockQuoteClient.getStockQuotesAndStats(options.tickerSymbols).get();
			
			StringColumn[] columns = {
					new StringColumn("Symbol",8, AlignType.TOP_LEFT),
					new StringColumn("Name",20, AlignType.TOP_LEFT),
					new StringColumn("Price",10, AlignType.TOP_RIGHT),
					new StringColumn("Mkt Cap",8, AlignType.TOP_RIGHT),
					new StringColumn("P/E",8, AlignType.TOP_RIGHT),
					new StringColumn("EPS",10, AlignType.TOP_RIGHT),
					new StringColumn("Yield",6, AlignType.TOP_RIGHT),
					new StringColumn("Day Low",10, AlignType.TOP_RIGHT),
					new StringColumn("Day High",10, AlignType.TOP_RIGHT),
					new StringColumn("52wk Low",10, AlignType.TOP_RIGHT),
					new StringColumn("52wk High",10, AlignType.TOP_RIGHT)
			};
			
			List<Row> rows = new ArrayList<>();
			for (StockQuote quote : stockQuotes.get()) {
				Row row = new Row(quote.getSymbol(), quote.getName(), quote.getCurrentPrice(), quote.getMarketCap(), quote.getPe(), quote.getEps(), quote.getYield(), quote.getDayLow(), quote.getDayHigh(),
						quote.getFiftyTwoWeekLow(), quote.getFiftyTwoWeekHigh());
				rows.add(row);
				
			}
			
			outputs = new TableUtils()
					.addColumns(columns)
					.addRows(rows)
					.buildTableOutput();
			
		}
		return outputs;
	}

}

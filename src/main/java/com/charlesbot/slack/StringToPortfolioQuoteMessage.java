package com.charlesbot.slack;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.core.convert.converter.Converter;

import com.brsanthu.dataexporter.DataExporter;
import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.StringColumn;
import com.brsanthu.dataexporter.output.texttable.TextTableExporter;
import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

public class StringToPortfolioQuoteMessage implements Converter<String, PortfolioQuoteMessage> {

	private IexStockQuoteClient iexStockQuoteClient;
	
	public class Position {
		final String symbol;
		final BigDecimal quantity;
		final BigDecimal price;
		private StockQuote quote;
		
		public Position(String symbol, BigDecimal quantity, BigDecimal price) {
			super();
			this.symbol = symbol;
			this.quantity = quantity;
			this.price = price;
		}

		public StockQuote getQuote() {
			return quote;
		}

		public void setQuote(StockQuote quote) {
			this.quote = quote;
		}
	}

	private CommandLineParser parser;
	private Options options;

	public StringToPortfolioQuoteMessage(IexStockQuoteClient iexStockQuoteClient) {
		this.iexStockQuoteClient = iexStockQuoteClient;
		// create the command line parser
		parser = new DefaultParser();

		// configure options
		configureCommandLineOptions();
		
	}

	private void configureCommandLineOptions() {
		// create the Options
		options = new Options();
		options.addOption("?", "help", false, "prints this message");
	}

	@Override
	public PortfolioQuoteMessage convert(String text) {
		PortfolioQuoteMessage message = new PortfolioQuoteMessage();
		try {
			CommandLine command = parser.parse(options, text.split("\\s+"));
			List<String> argList = command.getArgList();
			if (argList.size() < 1 || command.hasOption('?')) {
				String helpMessage = generateHelpMessage();
				message.setText("```"+helpMessage+"```");
			} else {
				Map<String, Position> positions = getPositions(command.getArgList());
				
				// get stock quotes for each key
				Set<String> keySet = positions.keySet();
				ArrayList<String> list = new ArrayList<>(keySet);
				Optional<StockQuotes> stockQuotes = iexStockQuoteClient.getStockQuotes(list);
				
				// associate the StockQuote with each position
				stockQuotes.ifPresent(quotes ->
					quotes.get().forEach((quote) ->
						positions.get(quote.getSymbol()).setQuote(quote)
					)
				);
				
				// build a table string with the positions
				StringBuilder sb = new StringBuilder();
				sb.append("```");
				
				StringWriter stringWriter = new StringWriter();
				DataExporter exporter = new TextTableExporter(stringWriter);
				exporter.addColumns(
						new StringColumn("Symbol",8, AlignType.TOP_LEFT),
						new StringColumn("Name",20, AlignType.TOP_LEFT),
						new StringColumn("Price",10, AlignType.TOP_RIGHT),
						new StringColumn("Change",8, AlignType.TOP_RIGHT),
						new StringColumn("Change %",8, AlignType.TOP_RIGHT),
						new StringColumn("Shares",8, AlignType.TOP_RIGHT),
						new StringColumn("Cost basis",10, AlignType.TOP_RIGHT),
						new StringColumn("Mkt value",10, AlignType.TOP_RIGHT),
						new StringColumn("Gain",8, AlignType.TOP_RIGHT),
						new StringColumn("Gain %",8, AlignType.TOP_RIGHT),
						new StringColumn("Day Gain",10, AlignType.TOP_RIGHT)
						);
				
				for (Position position : positions.values()) {
					
					BigDecimal costBasis = position.quantity.multiply(position.price);
					BigDecimal marketValue = position.quantity.multiply(position.getQuote().getPriceAsBigDecimal());
					BigDecimal gain = marketValue.subtract(costBasis);
					BigDecimal gainPercent = gain.divide(costBasis, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
					BigDecimal dayGain = position.quantity.multiply(position.getQuote().getChangeAsBigDecimal());
					
					exporter.addRow(
							position.symbol, 
							position.getQuote().getName(), 
							position.getQuote().getPrice(),
							position.getQuote().getChange(),
							position.getQuote().getChangeInPercent()+"%",
							position.quantity,
							costBasis,
							marketValue,
							gain,
							gainPercent+"%",
							dayGain
					);
					
				}
				exporter.finishExporting();
				sb.append(stringWriter.toString());
				sb.append("```");
				
				
				message.setText(sb.toString());
			}
		} catch (ParseException e) {
			String failureMessage = e.getMessage();
			String helpMessage = generateHelpMessage();
			message.setText("```"+failureMessage + "\n" + helpMessage+"```");
		}
		return message;
	}

	private Map<String, Position> getPositions(List<String> argList) {
		Map<String, Position> map = new LinkedHashMap<>();
		
		// for each string
		for (String arg : argList) {
			// split on ,
			String[] positionStrings = arg.split(",");
			
			// create a position
			String symbol = positionStrings[0].toUpperCase();
			BigDecimal quantity = new BigDecimal(positionStrings[1]);
			BigDecimal price = new BigDecimal(positionStrings[2]);
			Position position = new Position(symbol, quantity, price);
			
			// populate linked hash map so insertion order is preserved
			map.put(position.symbol, position);
		}
		
		return map;
	}

	private String generateHelpMessage() {
		HelpFormatter formatter = new HelpFormatter();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		formatter.printHelp(pw, 80, "!pf <SYMBOL>,<QUANTITY>,<PRICE>[ <SYMBOL>,<QUANTITY>,<PRICE>]+", "SYMBOL is the IEX ticker for the stock or index; QUANTITY is the number of shares; PRICE is the purchase price", options, 0, 3, "", true);
		String string = sw.toString();
		return string;
	}

}

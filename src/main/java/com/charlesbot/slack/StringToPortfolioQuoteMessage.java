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

import javax.inject.Inject;

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
import com.charlesbot.google.GoogleFinanceClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class StringToPortfolioQuoteMessage implements Converter<String, PortfolioQuoteMessage> {

	private GoogleFinanceClient googleFinanceClient;
	
	RangeMap<Double, String> percentRanges;
	
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

	public StringToPortfolioQuoteMessage(GoogleFinanceClient googleFinanceClient) {
		this.googleFinanceClient = googleFinanceClient;
		// create the command line parser
		parser = new DefaultParser();

		// configure options
		configureCommandLineOptions();
		
		// initialize the percent ranges
		percentRanges = TreeRangeMap.create();
		percentRanges.put(Range.atLeast(10d), "🢁");       // [10, +∞)
		percentRanges.put(Range.closedOpen(6d,10d), "🡱"); // [6, 10)
		percentRanges.put(Range.closedOpen(3d,6d), "🡡");  // [3, 6)
		percentRanges.put(Range.closedOpen(1d,3d), "🡑");  // [1, 3)
		percentRanges.put(Range.open(0d,1d), "🠑");        // (0, 1)
		percentRanges.put(Range.closed(0d,0d), "-");       // [0, 0]
		percentRanges.put(Range.open(-1d,0d), "🠓");         // (-1, 0)
		percentRanges.put(Range.openClosed(-3d,-1d), "🡓");  // (-3, -1]
		percentRanges.put(Range.openClosed(-6d,-3d), "🡣");  // (-6, -3]
		percentRanges.put(Range.openClosed(-10d,-6d), "🡳"); // (-10, -6]
		percentRanges.put(Range.atMost(-10d), "🢃");         // (-∞, -10]
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
			if (argList.size() != 2 || command.hasOption('?')) {
				String helpMessage = generateHelpMessage();
				message.setText("```"+helpMessage+"```");
			} else {
				command.getArgList().remove(0);
				Map<String, Position> positions = getPositions(command.getArgList());
				
				// get stock quotes for each key
				Set<String> keySet = positions.keySet();
				ArrayList<String> list = new ArrayList<>(keySet);
				Optional<StockQuotes> stockQuotes = googleFinanceClient.getStockQuotes(list);
				
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
						new StringColumn(" ",1, AlignType.TOP_LEFT),
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
					BigDecimal gainPercent = gain.divide(costBasis, 2, RoundingMode.HALF_UP);
					BigDecimal dayGain = position.quantity.multiply(position.getQuote().getChangeAsBigDecimal());
					
					exporter.addRow(
							determineRangeString(position.getQuote()),
							position.symbol, 
							position.getQuote().getName(), 
							position.getQuote().getPrice(),
							position.getQuote().getChange(),
							position.getQuote().getChangeInPercent(),
							position.quantity,
							costBasis,
							marketValue,
							gain,
							gainPercent,
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
		formatter.printHelp(pw, 80, "!pf <SYMBOL>,<QUANTITY>,<PRICE>[ <SYMBOL>,<QUANTITY>,<PRICE>]+", "SYMBOL is the Google Finance ticker for the stock or index; QUANTITY is the number of shares; PRICE is the purchase price", options, 0, 3, "", true);
		String string = sw.toString();
		return string;
	}

	String determineRangeString(StockQuote quote) {
		
		double totalChangeInPercent = quote.getTotalChangeInPercent();
		
		String rangeString = percentRanges.get(totalChangeInPercent);
		
		return rangeString;
	}
}

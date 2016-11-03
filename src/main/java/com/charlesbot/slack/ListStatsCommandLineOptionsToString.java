package com.charlesbot.slack;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.brsanthu.dataexporter.DataExporter;
import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.StringColumn;
import com.brsanthu.dataexporter.output.texttable.TextTableExporter;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListStatsCommandLineOptions;
import com.charlesbot.google.GoogleFinanceClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.charlesbot.model.Transaction;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@Component
public class ListStatsCommandLineOptionsToString implements Converter<ListStatsCommandLineOptions, String> {

	private static final Logger logger = LoggerFactory.getLogger(ListStatsCommandLineOptionsToString.class);
	
	@Autowired
	private WatchListRepository watchListRepository;
	
	@Autowired
	private GoogleFinanceClient googleFinanceClient;
	
	public ListStatsCommandLineOptionsToString() {
	}

	public ListStatsCommandLineOptionsToString(WatchListRepository watchListRepository) {
		this.watchListRepository = watchListRepository;
	}

	@Override
	public String convert(ListStatsCommandLineOptions options) {
		StringBuilder output = new StringBuilder();
		if (options.isHelp()) {
			output.append("```");
			for (String error: options.getErrors()) {
				output.append(error + "\n");
			}
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append(helpMessage + "```");
		} else if (!options.getWarnings().isEmpty()) {
			for (String warning : options.getWarnings()) { 
				output.append(warning + "\n");
			}
		} else {
			
			WatchList watchList = watchListRepository.findByUserIdAndName(options.userId, options.watchListName);
		
			List<String> symbols = watchList.transactions.stream()
				.map(t -> t.getSymbol().toUpperCase())
				.distinct()
				.collect(Collectors.toList())
				;
			
			// get stock quotes for each key
			Optional<StockQuotes> stockQuotes = googleFinanceClient.getStockQuotes(symbols);
			
			// build a map of each ticker symbol to its quote
			Map<String, StockQuote> stockQuotesMap = new HashMap<>();
			for (StockQuote q : stockQuotes.get().get()) {
				stockQuotesMap.put(q.getSymbol().toUpperCase(), q);
			}
			
			// build a map with all of the transactions
			ListMultimap<String, Transaction> transactionsMap = ArrayListMultimap.create();
			for (Transaction t : watchList.transactions) {
				transactionsMap.put(t.getSymbol().toUpperCase(), t);
			}
			
			// create positions from the transactions
			Map<String, Position> positions = new HashMap<>();
			for (String s : transactionsMap.keySet()) {
				Position position = new Position(s.toUpperCase());
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal totalPrice = BigDecimal.ZERO;
				for (Transaction t : transactionsMap.get(s)) {
					if (t != null) {
						if (t.quantity != null) {
							quantity = quantity.add(t.quantity);
							if (t.price != null) {
								totalPrice = totalPrice.add(t.price.multiply(t.quantity));
							} else {
								logger.warn("No price available on transaction {}", t);
							}
						} else {
							logger.warn("No quantity available on transaction {}", t);
						}
					} else {
						logger.warn("No transaction found for symbol {}", s);
					}
				}
				
				if (!quantity.equals(BigDecimal.ZERO)) {
					position.price = totalPrice.divide(quantity, 2, RoundingMode.HALF_UP);
				} else {
					position.price = BigDecimal.ZERO;
					logger.warn("Setting position price to zero to avoid dividing by zero {}", position);
				}
				position.quantity = quantity;
				position.setQuote(stockQuotesMap.get(position.symbol));
				positions.put(position.symbol, position);
			}
			
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
				if (!BigDecimal.ZERO.equals(position.price) && !BigDecimal.ZERO.equals(position.quantity)) {
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
				
			}
			exporter.finishExporting();
			sb.append(stringWriter.toString());
			sb.append("```");
			
			output.append(sb);
		}
					
		return output.toString();
	}

	public class Position {
		final String symbol;
		BigDecimal quantity;
		BigDecimal price;
		private StockQuote quote;
		
		public Position(String symbol) {
			super();
			this.symbol = symbol;
		}

		public StockQuote getQuote() {
			return quote;
		}

		public void setQuote(StockQuote quote) {
			this.quote = quote;
		}
	}
	
}

package com.charlesbot.slack;

import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.reverseOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.StringColumn;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListStatsCommandLineOptions;
import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.charlesbot.model.Transaction;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class ListStatsCommandLineOptionsToStrings implements CommandConverter<ListStatsCommandLineOptions> {

	private static final Logger logger = LoggerFactory.getLogger(ListStatsCommandLineOptionsToStrings.class);

	private WatchListRepository watchListRepository;
	private IexStockQuoteClient iexStockQuoteClient;

	public ListStatsCommandLineOptionsToStrings(WatchListRepository watchListRepository, IexStockQuoteClient iexStockQuoteClient) {
		this.watchListRepository = watchListRepository;
		this.iexStockQuoteClient = iexStockQuoteClient;
	}

	@Override
	public List<String> convert(ListStatsCommandLineOptions options) {
		List<String> outputs = new ArrayList<>();

		if (options.isHelp()) {
			StringBuilder output = new StringBuilder();
			output.append("```");
			for (String error : options.getErrors()) {
				output.append(error + "\n");
			}
			String helpMessage = CommandLineProcessor.generateHelpMessage(options);
			output.append(helpMessage + "```");
			outputs.add(output.toString());
		} else if (!options.getWarnings().isEmpty()) {
			StringBuilder output = new StringBuilder();
			for (String warning : options.getWarnings()) {
				output.append(warning + "\n");
			}
			outputs.add(output.toString());
		} else {
			outputs = buildStatsTable(options);
		}

		return outputs;
	}

	private List<String> buildStatsTable(ListStatsCommandLineOptions options) {
		StringColumn[] columns = { new StringColumn("symbol", "Symbol", 8, AlignType.TOP_LEFT),
				new StringColumn("name", "Name", 20, AlignType.TOP_LEFT),
				new StringColumn("price", "Price", 10, AlignType.TOP_RIGHT),
				new StringColumn("change", "Change", 8, AlignType.TOP_RIGHT),
				new StringColumn("changeInPercent", "Change %", 8, AlignType.TOP_RIGHT),
				new StringColumn("quantity", "Shares", 8, AlignType.TOP_RIGHT),
				new StringColumn("formattedCostBasis", "Cost basis", 10, AlignType.TOP_RIGHT),
				new StringColumn("formattedMarketValue", "Mkt value", 10, AlignType.TOP_RIGHT),
				new StringColumn("formattedGain", "Gain", 9, AlignType.TOP_RIGHT),
				new StringColumn("formattedGainPercent", "Gain %", 8, AlignType.TOP_RIGHT),
				new StringColumn("formattedListPercent", "List%", 5, AlignType.TOP_RIGHT),
				new StringColumn("formattedDayGain", "Day Gain", 10, AlignType.TOP_RIGHT) };

		Iterable<Position> positions = getPositionsList(options.userId, options.watchListName);
		List<ListStatsRow> rows = buildRows(positions, options.shortened);

		List<String> outputs = new TableUtils().addColumns(columns).addBeanRows(rows).buildTableOutput();

		return outputs;
	}

	private List<ListStatsRow> buildRows(Iterable<Position> positions, boolean totalsOnly) {
		List<ListStatsRow> rows = new ArrayList<>();
		BigDecimal marketValueTotal = BigDecimal.ZERO;
		for (Position position : positions) {
			ListStatsRow row = new ListStatsRow();
			row.setSymbol(position.symbol);

			if (!BigDecimal.ZERO.equals(position.price) && !BigDecimal.ZERO.equals(position.quantity)) {
				BigDecimal costBasis = position.quantity.multiply(position.price);
				if (position.getQuote() != null && position.getQuote().getPriceAsBigDecimal() != null) {
					BigDecimal marketValue = position.quantity.multiply(position.getQuote().getPriceAsBigDecimal());
					marketValueTotal = marketValueTotal.add(marketValue);
					BigDecimal gain = marketValue.subtract(costBasis);
					BigDecimal gainPercent = gain.divide(costBasis, 6, RoundingMode.HALF_UP);
					if (position.getQuote().getChangeAsBigDecimal() != null) {
						BigDecimal dayGain = position.quantity.multiply(position.getQuote().getChangeAsBigDecimal());
						row.setDayGain(dayGain);
					}
					row.setCostBasis(costBasis);
					row.setMarketValue(marketValue);
					row.setGain(gain);
					row.setGainPercent(gainPercent);
				}
			}

			if (position.quantity != null) {
				row.setQuantity(position.quantity);
			}

			if (position.getQuote() != null) {
				row.setName(position.getQuote().getName());
				row.setPrice(position.getQuote().getPrice());
				row.setChange(position.getQuote().getChange());
				row.setChangeInPercent(position.getQuote().getChangeInPercent());
			}

			rows.add(row);

		}

		// handle totals and list %
		ListStatsRow totalRow = new ListStatsRow(" ");
		totalRow.setSymbol("Total:");
		totalRow.setCostBasis(BigDecimal.ZERO);
		totalRow.setDayGain(BigDecimal.ZERO);
		totalRow.setGain(BigDecimal.ZERO);
		totalRow.setMarketValue(BigDecimal.ZERO);
		for (ListStatsRow row : rows) {
			if (row.getMarketValue() != null) {
				row.setListPercent(row.getMarketValue().divide(marketValueTotal, 4, RoundingMode.HALF_UP));
				totalRow.setCostBasis(totalRow.getCostBasis().add(row.getCostBasis()));
				totalRow.setGain(totalRow.getGain().add(row.getGain()));
				if (row.getDayGain() != null) {
				totalRow.setDayGain(totalRow.getDayGain().add(row.getDayGain()));
				}
				totalRow.setMarketValue(totalRow.getMarketValue().add(row.getMarketValue()));
			}
		}
		if (totalRow.getGain() != null && totalRow.getCostBasis() != null && !BigDecimal.ZERO.equals(totalRow.getCostBasis())) {
			totalRow.setGainPercent(totalRow.getGain().divide(totalRow.getCostBasis(), 7, RoundingMode.HALF_UP));
		}
		
		if (totalsOnly == true) {
			rows = new ArrayList<>();
		} else {
			rows = rows.stream().sorted(
					Comparator.comparing(ListStatsRow::getGainPercent, nullsFirst(reverseOrder())).thenComparing(ListStatsRow::getSymbol))
					.collect(Collectors.toList());
		}
		rows.add(totalRow);
		
		return rows;
	}

	private Iterable<Position> getPositionsList(String userId, String watchListName) {
		WatchList watchList = watchListRepository.findByUserIdAndName(userId, watchListName);

		List<String> symbols = watchList.transactions.stream().map(t -> t.getSymbol().toUpperCase()).distinct()
				.collect(Collectors.toList());

		// get stock quotes for each key
		Optional<StockQuotes> stockQuotes = iexStockQuoteClient.getStockQuotesAndStats(symbols);

		// build a map of each ticker symbol to its quote
		Map<String, StockQuote> stockQuotesMap = new HashMap<>();
		for (StockQuote q : stockQuotes.get().get()) {
			if (q != null && q.getSymbol() != null) {
				stockQuotesMap.put(q.getSymbol().toUpperCase(), q);
			}
		}

		// build a map with all of the transactions
		// - if the symbol name includes the exchange mnemonic (e.g. nyse:shop)
		// then strip the exchange from the symbol)
		ListMultimap<String, Transaction> transactionsMap = ArrayListMultimap.create();
		for (Transaction t : watchList.transactions) {
			String symbol = t.getSymbol();
			if (symbol != null && symbol.contains(":")) {
				String[] symbolStrings = symbol.split(":");
				symbol = symbolStrings[1];

			}
			transactionsMap.put(symbol.toUpperCase(), t);
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

		return positions.values();
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

		@Override
		public String toString() {
			return "Position [symbol=" + symbol + "]";
		}

	}

}

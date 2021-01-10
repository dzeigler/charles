package com.charlesbot.slack;

import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.reverseOrder;

import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.StringColumn;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.cli.ListStatsCommandLineOptions;
import com.charlesbot.iex.IexStockQuoteClient;
import com.charlesbot.model.Position;
import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.charlesbot.service.PositionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ListStatsCommandLineOptionsToStrings implements CommandConverter<ListStatsCommandLineOptions> {

	private static final Logger logger = LoggerFactory.getLogger(ListStatsCommandLineOptionsToStrings.class);

	private final WatchListRepository watchListRepository;
	private final PositionService positionService;
	private final IexStockQuoteClient iexStockQuoteClient;

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
		List<String> outputs = new ArrayList<>();
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

		WatchList watchList = watchListRepository.findByUserIdAndName(options.userId, options.watchListName);
		if (watchList == null) {
			outputs.add("No list named " + options.watchListName + " for that user.");
		} else {
			Collection<Position> positions = positionService.getPositionsList(watchList).stream()
					// omit closed positions
					.filter(p -> BigDecimal.ZERO.compareTo(p.getQuantity()) != 0)
					.collect(Collectors.toList());
			setPositionQuotes(positions);
			List<ListStatsRow> rows = buildRows(positions, options.shortened, options.orderByListPercent);

			outputs = new TableUtils().addColumns(columns).addBeanRows(rows).buildTableOutput();
		}
		return outputs;
	}

	private void setPositionQuotes(Collection<Position> positions) {
		List<String> symbols = positions.stream().map(p -> p.getSymbol().toUpperCase()).distinct()
				.collect(Collectors.toList());

		// get stock quotes for each key
		Optional<StockQuotes> stockQuotes = iexStockQuoteClient.getStockQuotesAndStats(symbols);

		// build a map of each ticker symbol to its quote
		Map<String, StockQuote> stockQuotesMap = stockQuotes.map(quotes ->
				quotes.get().stream()
						.filter(q -> q != null && q.getSymbol() != null)
						.collect(Collectors.toMap(StockQuote::getSymbol, Function.identity()))
		).orElse(new HashMap<>());

		positions.stream()
				.forEach(p -> p.setQuote(stockQuotesMap.get(p.getSymbol())));
	}

	private List<ListStatsRow> buildRows(Iterable<Position> positions, boolean totalsOnly, boolean orderByListPercent) {
		List<ListStatsRow> rows = new ArrayList<>();
		BigDecimal marketValueTotal = BigDecimal.ZERO;
		for (Position position : positions) {
			ListStatsRow row = new ListStatsRow();
			row.setSymbol(position.getSymbol());

			if (!BigDecimal.ZERO.equals(position.getPrice()) && !BigDecimal.ZERO.equals(position.getQuantity())) {
				BigDecimal costBasis = position.getQuantity().multiply(position.getPrice());
				if (position.getQuote() != null && position.getQuote().getPriceAsBigDecimal() != null) {
					BigDecimal marketValue = position.getQuantity().multiply(position.getQuote().getPriceAsBigDecimal());
					marketValueTotal = marketValueTotal.add(marketValue);
					BigDecimal gain = marketValue.subtract(costBasis);
					BigDecimal gainPercent = gain.divide(costBasis, 6, RoundingMode.HALF_UP);
					if (position.getQuote().getChangeAsBigDecimal() != null) {
						BigDecimal dayGain = position.getQuantity().multiply(position.getQuote().getChangeAsBigDecimal());
						row.setDayGain(dayGain);
					}
					row.setCostBasis(costBasis);
					row.setMarketValue(marketValue);
					row.setGain(gain);
					row.setGainPercent(gainPercent);
				}
			}

			if (position.getQuantity() != null) {
				row.setQuantity(position.getQuantity());
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
		} else if (orderByListPercent == true) {
			rows = rows.stream().sorted(
					Comparator.comparing(ListStatsRow::getListPercent, nullsFirst(reverseOrder()))
							.thenComparing(ListStatsRow::getSymbol))
					.collect(Collectors.toList());
		} else {
			rows = rows.stream().sorted(
					Comparator.comparing(ListStatsRow::getGainPercent, nullsFirst(reverseOrder()))
							.thenComparing(ListStatsRow::getSymbol))
					.collect(Collectors.toList());
		}
		rows.add(totalRow);
		
		return rows;
	}



}

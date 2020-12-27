package com.charlesbot.service;

import com.charlesbot.model.Position;
import com.charlesbot.model.Transaction;
import com.charlesbot.model.WatchList;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PositionService {

	public Collection<Position> getPositionsList(WatchList watchList) {

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
							log.warn("No price available on transaction {}", t);
						}
					} else {
						log.warn("No quantity available on transaction {}", t);
					}
				} else {
					log.warn("No transaction found for symbol {}", s);
				}
			}

			if (!quantity.equals(BigDecimal.ZERO)) {
				position.setPrice(totalPrice.divide(quantity, 2, RoundingMode.HALF_UP));
			} else {
				position.setPrice(BigDecimal.ZERO);
				log.warn("Setting position price to zero to avoid dividing by zero {}", position);
			}
			position.setQuantity(quantity);
			positions.put(position.getSymbol(), position);
		}

		return positions.values();
	}

}

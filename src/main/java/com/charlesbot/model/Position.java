package com.charlesbot.model;

import java.math.BigDecimal;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Position {
	@ToString.Include
	private final String symbol;
	private BigDecimal quantity;
	private BigDecimal price;
	private StockQuote quote;
}

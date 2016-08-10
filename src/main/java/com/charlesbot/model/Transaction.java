package com.charlesbot.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Joiner;

public class Transaction {

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private String symbol;
	public BigDecimal quantity;
	public BigDecimal price;
	public LocalDate date;

	public String toString(String delimiter) {
		return Joiner.on(delimiter).skipNulls().join(getSymbol(), quantity, price, date);

	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
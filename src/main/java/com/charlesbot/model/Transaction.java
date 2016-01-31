package com.charlesbot.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.common.base.Joiner;

public class Transaction {

	public String symbol;
	public BigDecimal quantity;
	public BigDecimal price;
	public LocalDate date;
	
	public String toString(String delimiter) {
		return Joiner.on(delimiter).skipNulls().join(symbol, quantity, price, date);
		
	}
}

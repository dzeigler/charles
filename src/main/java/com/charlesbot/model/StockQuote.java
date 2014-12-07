package com.charlesbot.model;

public class StockQuote {
	// c1
	private String change;
	// p2
	private String changeInPercent;
	// h0
	private String dayHigh;
	// g0
	private String dayLow;
	// m3
	private String fiftyDayMovingAverage;
	// l1 - last price
	private String price;
	// s0
	private String symbol;
	//  n0
	private String name;

	public StockQuote() {
	}


	public String getChange() {
		return change;
	}

	public String getChangeInPercent() {
		return changeInPercent;
	}


	public void setChangeInPercent(String changeInPercent) {
		this.changeInPercent = changeInPercent;
	}


	public String getDayHigh() {
		return dayHigh;
	}

	public String getDayLow() {
		return dayLow;
	}

	public String getMovingAverage50Day() {
		return fiftyDayMovingAverage;
	}

	public String getPrice() {
		return price;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public void setDayHigh(String dayHigh) {
		this.dayHigh = dayHigh;
	}

	public void setDayLow(String dayLow) {
		this.dayLow = dayLow;
	}

	public void setFiftyDayMovingAverage(String fiftyDayMovingAverage) {
		this.fiftyDayMovingAverage = fiftyDayMovingAverage;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockQuote [change=").append(change).append(", dayHigh=").append(dayHigh).append(", dayLow=")
				.append(dayLow).append(", fiftyDayMovingAverage=").append(fiftyDayMovingAverage).append(", price=")
				.append(price).append(", symbol=").append(symbol).append("]");
		return builder.toString();
	}

	public static String[] getHeader() {
		return new String[] { "change", "changeInPercent", "dayHigh", "dayLow", "fiftyDayMovingAverage", "price", "symbol", "name" };
	}

}
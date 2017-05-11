package com.charlesbot.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockQuote {
	// c1
	@JsonProperty("c")
	private String change;
	// p2
	@JsonProperty("cp")
	private String changeInPercent;
	// h0
	@JsonProperty("hi")
	private String dayHigh;
	// g0
	@JsonProperty("lo")
	private String dayLow;
	// m3
	private String fiftyDayMovingAverage;
	// l1 - last price
	@JsonProperty("l")
	private String price;
	// s0
	@JsonProperty("t")
	private String symbol;
	// n0
	@JsonProperty("name")
	private String name;

	@JsonProperty("mc")
	private String marketCap;
	@JsonProperty("pe")
	private String pe;
	@JsonProperty("eps")
	private String eps;
	@JsonProperty("hi52")
	private String fiftyTwoWeekHigh;
	@JsonProperty("lo52")
	private String fiftyTwoWeekLow;
	@JsonProperty("shares")
	private String shares;
	@JsonProperty("beta")
	private String beta;
	@JsonProperty("div")
	private String dividend;
	@JsonProperty("yld")
	private String yield;
	@JsonProperty("el")
	private String extendedHoursPrice;
	@JsonProperty("ec")
	private String extendedHoursChange;
	@JsonProperty("ecp")
	private String extendedHoursChangeInPercent;

	public StockQuote() {
	}

	public String getChange() {
		return change;
	}
	
	public BigDecimal getChangeAsBigDecimal() {
		try {
			return new BigDecimal(change);
		} catch (NumberFormatException nfe) {
			
		}
		return null;
	}
	
	public String getChangeInPercent() {
		return changeInPercent;
	}

	public void setChangeInPercent(String changeInPercent) {
		this.changeInPercent = changeInPercent;
	}

	public String getDayHigh() {
		if (StringUtils.isEmpty(dayHigh)) {
			return "-";
		}
		return dayHigh;
	}

	public String getDayLow() {
		if (StringUtils.isEmpty(dayLow)) {
			return "-";
		}
		return dayLow;
	}

	public String getMovingAverage50Day() {
		return fiftyDayMovingAverage;
	}

	public String getPrice() {
		return price;
	}
	
	public BigDecimal getPriceAsBigDecimal() {
		try {
			if (price != null) {
				DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
				decimalFormat.setParseBigDecimal(true);
				return (BigDecimal) decimalFormat.parse(price);
			}
		} catch (ParseException e) {
			// ignore exception
		}
		return null;
	}

	public String getCurrentPrice() {
		if (!StringUtils.isEmpty(extendedHoursPrice)) {
			return extendedHoursPrice;
		}
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

	public String getMarketCap() {
		if (StringUtils.isEmpty(marketCap)) {
			return "-";
		}
		return marketCap;
	}

	public void setMarketCap(String marketCap) {
		this.marketCap = marketCap;
	}

	public String getPe() {
		if (StringUtils.isEmpty(pe)) {
			return "-";
		}
		return pe;
	}

	public void setPe(String pe) {
		this.pe = pe;
	}

	public String getEps() {
		if (StringUtils.isEmpty(eps)) {
			return "-";
		}
		return eps;
	}

	public void setEps(String eps) {
		this.eps = eps;
	}

	public String getFiftyTwoWeekHigh() {
		if (StringUtils.isEmpty(fiftyTwoWeekHigh)) {
			return "-";
		}
		return fiftyTwoWeekHigh;
	}

	public void setFiftyTwoWeekHigh(String fiftyTwoWeekHigh) {
		this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
	}

	public String getFiftyTwoWeekLow() {
		if (StringUtils.isEmpty(fiftyTwoWeekLow)) {
			return "-";
		}
		return fiftyTwoWeekLow;
	}

	public void setFiftyTwoWeekLow(String fiftyTwoWeekLow) {
		this.fiftyTwoWeekLow = fiftyTwoWeekLow;
	}

	public String getShares() {
		return shares;
	}

	public void setShares(String shares) {
		this.shares = shares;
	}

	public String getBeta() {
		if (StringUtils.isEmpty(beta)) {
			return "-";
		}
		return beta;
	}

	public void setBeta(String beta) {
		this.beta = beta;
	}

	public String getExtendedHoursPrice() {
		return extendedHoursPrice;
	}

	public void setExtendedHoursPrice(String extendedHoursPrice) {
		this.extendedHoursPrice = extendedHoursPrice;
	}

	public String getExtendedHoursChange() {
		return extendedHoursChange;
	}

	public void setExtendedHoursChange(String extendedHoursChange) {
		this.extendedHoursChange = extendedHoursChange;
	}

	public String getExtendedHoursChangeInPercent() {
		return extendedHoursChangeInPercent;
	}

	public void setExtendedHoursChangeInPercent(String extendedHoursChangeInPercent) {
		this.extendedHoursChangeInPercent = extendedHoursChangeInPercent;
	}

	public String getFiftyDayMovingAverage() {
		return fiftyDayMovingAverage;
	}

	public String getDividend() {
		if (StringUtils.isEmpty(dividend)) {
			return "-";
		}
		return dividend;
	}

	public void setDividend(String dividend) {
		this.dividend = dividend;
	}

	public String getYield() {
		if (StringUtils.isEmpty(yield)) {
			return "-";
		}
		return yield;
	}

	public void setYield(String yield) {
		this.yield = yield;
	}
	
	public Double getTotalChangeInPercent() {
		double changeInPercent = 0d;
		try { 
			changeInPercent = new Double(getChangeInPercent());
		} catch (Exception nfe) {
			// ignore
		}
		
		double extendedHoursChangeInPercent = 0d;
		try { 
			extendedHoursChangeInPercent = new Double(getExtendedHoursChangeInPercent());
		} catch (Exception nfe) {
			// ignore
		}
		
		double totalChangeInPercent = changeInPercent + extendedHoursChangeInPercent;
		return totalChangeInPercent;
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
		return new String[] { "change", "changeInPercent", "dayHigh", "dayLow", "fiftyDayMovingAverage", "price",
				"symbol", "name" };
	}

}

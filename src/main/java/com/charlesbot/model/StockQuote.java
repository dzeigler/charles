package com.charlesbot.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockQuote {

	@JsonProperty("change")
	private String change;

	@JsonProperty("changePercent")
	private String changeInPercent;

	private String dayHigh;
	private String dayLow;

	@JsonProperty("latestPrice")
	private String price;

	@JsonProperty("symbol")
	private String symbol;

	@JsonProperty("companyName")
	private String name;

	@JsonProperty("marketCap")
	private String marketCap;
	@JsonProperty("peRatio")
	private String pe;
	private String eps;
	@JsonProperty("week52High")
	private String fiftyTwoWeekHigh;
	@JsonProperty("week52Low")
	private String fiftyTwoWeekLow;
	private String dividend;
	private String yield;
	private String extendedHoursPrice;
	private String extendedHoursChange;
	private String extendedHoursChangeInPercent;

	public StockQuote() {
	}

	public String getChange() {
		if (StringUtils.isEmpty(change)) {
			return "-";
		}
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
		if (StringUtils.isEmpty(changeInPercent)) {
			return "-";
		}
		return changeInPercent;
	}

	public void setChangeInPercent(String changeInPercent) {
		if (StringUtils.isNotBlank(changeInPercent)) {
			changeInPercent = changeInPercent.replace("%", "");
		}
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

	public String getPrice() {
		if (StringUtils.isEmpty(price)) {
			return "-";
		}
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

	public void setPrice(String price) {
		this.price = price;
	}

	public void setSymbol(String symbol) {
		if (StringUtils.isNotEmpty(symbol)) {
			symbol = symbol.toUpperCase();
		}
		this.symbol = symbol;
	}

	public String getName() {
		if (StringUtils.isEmpty(name)) {
			return "-";
		}
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
				.append(dayLow).append(", price=").append(price).append(", symbol=").append(symbol).append("]");
		return builder.toString();
	}

	public static String[] getHeader() {
		return new String[] { "change", "changeInPercent", "dayHigh", "dayLow", "fiftyDayMovingAverage", "price",
				"symbol", "name" };
	}

}

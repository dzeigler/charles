package com.charlesbot.model;

import java.text.NumberFormat;

public class CurrencyQuote {

	private String fromCurrency;
	private String fromSymbol;
	private String toCurrency;
	private String toSymbol;
	private String market;
	private String price;
	private String lastUpdate;
	private String open24Hour;
	private String high24Hour;
	private String low24Hour;
	private String change24Hour;
	private String changePercent24Hour;
	private String supply;
	private String marketCap;

	public String getFromCurrency() {
		return fromCurrency;
	}

	public void setFromCurrency(String fromCurrency) {
		this.fromCurrency = fromCurrency;
	}

	public String getFromSymbol() {
		return fromSymbol;
	}

	public void setFromSymbol(String fromSymbol) {
		this.fromSymbol = fromSymbol;
	}

	public String getToCurrency() {
		return toCurrency;
	}

	public void setToCurrency(String toCurrency) {
		this.toCurrency = toCurrency;
	}

	public String getToSymbol() {
		return toSymbol;
	}

	public void setToSymbol(String toSymbol) {
		this.toSymbol = toSymbol;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getOpen24Hour() {
		return open24Hour;
	}

	public void setOpen24Hour(String open24Hour) {
		this.open24Hour = open24Hour;
	}

	public String getHigh24Hour() {
		return high24Hour;
	}

	public void setHigh24Hour(String high24Hour) {
		this.high24Hour = high24Hour;
	}

	public String getLow24Hour() {
		return low24Hour;
	}

	public void setLow24Hour(String low24Hour) {
		this.low24Hour = low24Hour;
	}

	public String getChange24Hour() {
		return change24Hour;
	}

	public void setChange24Hour(String change24Hour) {
		this.change24Hour = change24Hour;
	}

	public String getChangePercent24Hour() {
		return changePercent24Hour;
	}

	public void setChangePercent24Hour(String changePercent24Hour) {
		this.changePercent24Hour = changePercent24Hour;
	}

	public String getSupply() {
		return supply;
	}

	public void setSupply(String supply) {
		this.supply = supply;
	}

	public String getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(String marketCap) {
		this.marketCap = marketCap;
	}
	
	public Double getTotalChangeInPercent() {
		double changeInPercent = 0d;
		try { 
			NumberFormat format = NumberFormat.getInstance();
			Number number = format.parse(getChangePercent24Hour());
			changeInPercent = number.doubleValue();
		} catch (Exception nfe) {
			// ignore
		}
		
		double totalChangeInPercent = changeInPercent;
		return totalChangeInPercent;
	}

	@Override
	public String toString() {
		return "CurrencyQuote [fromCurrency=" + fromCurrency + ", fromSymbol=" + fromSymbol + ", toCurrency="
				+ toCurrency + ", toSymbol=" + toSymbol + ", market=" + market + ", price=" + price + ", lastUpdate="
				+ lastUpdate + ", open24Hour=" + open24Hour + ", high24Hour=" + high24Hour + ", low24Hour=" + low24Hour
				+ ", change24Hour=" + change24Hour + ", changePercent24Hour=" + changePercent24Hour + ", supply="
				+ supply + ", marketCap=" + marketCap + "]";
	}

}

package com.charlesbot.slack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ListStatsRow {

	private final static String DEFAULT_VALUE = "N/A";
	
	private String symbol = DEFAULT_VALUE;
	private String name = DEFAULT_VALUE;
	private String price = DEFAULT_VALUE;
	private String change = DEFAULT_VALUE;
	private String changeInPercent = DEFAULT_VALUE;
	private String quantity = DEFAULT_VALUE;
	private BigDecimal costBasis;
	private BigDecimal marketValue;
	private BigDecimal gain;
	private BigDecimal gainPercent;
	private BigDecimal dayGain;
	private BigDecimal listPercent;

	public String getSymbol() {
		return symbol;
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public String getChangeInPercent() {
		return changeInPercent;
	}

	public void setChangeInPercent(String changeInPercent) {
		this.changeInPercent = changeInPercent + "%";
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity.toString();
	}

	public BigDecimal getCostBasis() {
		return costBasis;
	}

	public String getFormattedCostBasis() {
		return (costBasis == null ? DEFAULT_VALUE : costBasis.toString());
	}
	
	public void setCostBasis(BigDecimal costBasis) {
		this.costBasis = costBasis;
	}

	public BigDecimal getMarketValue() {
		return marketValue;
	}
	
	public String getFormattedMarketValue() {
		return (marketValue == null ? DEFAULT_VALUE : marketValue.toString());
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getGain() {
		return gain;
	}
	
	public String getFormattedGain() {
		return (gain == null ? DEFAULT_VALUE : gain.toString());
	}

	public void setGain(BigDecimal gain) {
		this.gain = gain;
	}

	public BigDecimal getGainPercent() {
		return gainPercent;
	}
	
	public String getFormattedGainPercent() {
		return (gainPercent == null ? DEFAULT_VALUE : gainPercent.toString() + "%");
	}

	public void setGainPercent(BigDecimal gainPercent) {
		this.gainPercent = gainPercent;
	}

	public BigDecimal getDayGain() {
		return dayGain;
	}
	
	public String getFormattedDayGain() {
		return (dayGain == null ? DEFAULT_VALUE : dayGain.toString());
	}

	public void setDayGain(BigDecimal dayGain) {
		this.dayGain = dayGain;
	}

	public BigDecimal getListPercent() {
		return listPercent;
	}

	public String getFormattedListPercent() {
		
		if (listPercent == null) {
			return DEFAULT_VALUE;
		} 
		DecimalFormat decimalFormat = new DecimalFormat("#0.#%");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		return decimalFormat.format(listPercent);

	}

	public void setListPercent(BigDecimal listPercent) {
		this.listPercent = listPercent;
	}

}

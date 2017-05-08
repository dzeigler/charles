package com.charlesbot.slack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ListStatsRow {

	private final static String DEFAULT_VALUE = "N/A";
	
	private String defaultValue;
	
	private String symbol;
	private String name;
	private String price;
	private String change;
	private String changeInPercent;
	private String quantity;
	private BigDecimal costBasis;
	private BigDecimal marketValue;
	private BigDecimal gain;
	private BigDecimal gainPercent;
	private BigDecimal dayGain;
	private BigDecimal listPercent;

	

	public ListStatsRow() {
		this(DEFAULT_VALUE);
	}
	
	private String getDefaultValue() {
		return defaultValue;
	}

	public ListStatsRow(String defaultValue)  {
		this.defaultValue = defaultValue;
	}
	
	public String getSymbol() {
		return (symbol == null ? getDefaultValue() : symbol);
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name == null ? getDefaultValue() : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price == null ? getDefaultValue() : price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getChange() {
		return change == null ? getDefaultValue() : change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public String getChangeInPercent() {
		return changeInPercent == null ? getDefaultValue() : changeInPercent;
	}

	public void setChangeInPercent(String changeInPercent) {
		this.changeInPercent = changeInPercent + "%";
	}

	public String getQuantity() {
		return quantity == null ? getDefaultValue() : quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity.toString();
	}

	public BigDecimal getCostBasis() {
		return costBasis;
	}

	public String getFormattedCostBasis() {
		return (costBasis == null ? defaultValue : costBasis.toString());
	}
	
	public void setCostBasis(BigDecimal costBasis) {
		this.costBasis = costBasis;
	}

	public BigDecimal getMarketValue() {
		return marketValue;
	}
	
	public String getFormattedMarketValue() {
		return (marketValue == null ? defaultValue : marketValue.toString());
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getGain() {
		return gain;
	}
	
	public String getFormattedGain() {
		return (gain == null ? defaultValue : gain.toString());
	}

	public void setGain(BigDecimal gain) {
		this.gain = gain;
	}

	public BigDecimal getGainPercent() {
		return gainPercent;
	}
	
	public String getFormattedGainPercent() {
		
		if (gainPercent == null) {
			return defaultValue;
		} 
		DecimalFormat decimalFormat = new DecimalFormat("###0.#%");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		return decimalFormat.format(gainPercent);
	}

	public void setGainPercent(BigDecimal gainPercent) {
		this.gainPercent = gainPercent;
	}

	public BigDecimal getDayGain() {
		return dayGain;
	}
	
	public String getFormattedDayGain() {
		return (dayGain == null ? defaultValue : dayGain.toString());
	}

	public void setDayGain(BigDecimal dayGain) {
		this.dayGain = dayGain;
	}

	public BigDecimal getListPercent() {
		return listPercent;
	}

	public String getFormattedListPercent() {
		
		if (listPercent == null) {
			return defaultValue;
		} 
		DecimalFormat decimalFormat = new DecimalFormat("#0.#%");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		return decimalFormat.format(listPercent);

	}

	public void setListPercent(BigDecimal listPercent) {
		this.listPercent = listPercent;
	}

}

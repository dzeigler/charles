package com.charlesbot.cli;

public class CurrencyChartOption {

	private final String timeSpan;
	private final String theme;

	public CurrencyChartOption(String timeSpan, String theme) {
		this.timeSpan = timeSpan;
		this.theme = theme;
	}

	public String getTimeSpan() {
		return timeSpan;
	}

	public String getTheme() {
		return theme;
	}

}

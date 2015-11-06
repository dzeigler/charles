package com.charlesbot.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Field {

	private String title;
	private String value;

	@JsonProperty("short")
	private boolean isShort;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isShort() {
		return isShort;
	}

	public void setShort(boolean isShort) {
		this.isShort = isShort;
	}

}

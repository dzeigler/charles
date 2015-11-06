package com.charlesbot.slack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties
public class SlackIncomingMessageResponse {

	private String ok;
	@JsonInclude(Include.NON_NULL)
	private String error;

	public String getOk() {
		return ok;
	}

	public void setOk(String ok) {
		this.ok = ok;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}

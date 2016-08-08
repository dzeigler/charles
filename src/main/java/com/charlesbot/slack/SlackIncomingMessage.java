package com.charlesbot.slack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Representation of the message object that is sent to slack
 */
public abstract class SlackIncomingMessage {

	@JsonInclude(Include.NON_NULL)
	private String text;
	@JsonInclude(Include.NON_NULL)
	private String channel;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}


}

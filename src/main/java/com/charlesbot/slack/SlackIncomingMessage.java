package com.charlesbot.slack;

/**
 * Representation of the message object that is sent to slack
 */
public class SlackIncomingMessage {

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

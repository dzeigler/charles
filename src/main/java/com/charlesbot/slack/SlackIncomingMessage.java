package com.charlesbot.slack;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Representation of the message object that is sent to slack
 */
public abstract class SlackIncomingMessage {

	private String text;
	
	@JsonInclude(Include.NON_NULL)
	private List<Attachment> attachments;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}

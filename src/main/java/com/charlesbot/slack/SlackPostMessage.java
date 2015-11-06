package com.charlesbot.slack;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public abstract class SlackPostMessage {

	private String token;
	private String channel;
	private String text;
	@JsonInclude(Include.NON_NULL)
	private String username;
	@JsonInclude(Include.NON_NULL)
	private String parse; // full or none
	@JsonInclude(Include.NON_NULL)
	private String linkNames; // 1 is true
	@JsonInclude(Include.NON_NULL)
	private List<Attachment> attachments;
	@JsonInclude(Include.NON_NULL)
	private boolean unfurlLinks;
	@JsonInclude(Include.NON_NULL)
	private boolean unfurlMedia;
	@JsonInclude(Include.NON_NULL)
	private String iconUrl;
	@JsonInclude(Include.NON_NULL)
	private String iconEmoji;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getParse() {
		return parse;
	}

	public void setParse(String parse) {
		this.parse = parse;
	}

	public String getLinkNames() {
		return linkNames;
	}

	public void setLinkNames(String linkNames) {
		this.linkNames = linkNames;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public boolean isUnfurlLinks() {
		return unfurlLinks;
	}

	public void setUnfurlLinks(boolean unfurlLinks) {
		this.unfurlLinks = unfurlLinks;
	}

	public boolean isUnfurlMedia() {
		return unfurlMedia;
	}

	public void setUnfurlMedia(boolean unfurlMedia) {
		this.unfurlMedia = unfurlMedia;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIconEmoji() {
		return iconEmoji;
	}

	public void setIconEmoji(String iconEmoji) {
		this.iconEmoji = iconEmoji;
	}

	@Override
	public String toString() {
		return "SlackPostMessage [token=" + token + ", channel=" + channel + ", text=" + text + ", username=" + username
				+ ", attachments=" + attachments + "]";
	}
	
	
}

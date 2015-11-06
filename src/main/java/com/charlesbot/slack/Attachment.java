package com.charlesbot.slack;

import java.util.List;

public class Attachment {

	/**
	 * Required plain-text summary of the attachment.
	 */
	private String fallback;

	/**
	 * Good, warning, danger, or any hex color code
	 */
	private String color;

	/**
	 * Optional text that appears above the attachment block
	 */
	private String pretext;

	private String authorName;
	private String authorLink;
	private String authorIcon;

	/**
	 * Title of the attachment
	 */
	private String title;
	private String titleLink;

	/**
	 * Optional text that appears within the attachment
	 */
	private String text;
	private List<Field> fields;
	private String imageUrl;
	private String thumbUrl;

	public String getFallback() {
		return fallback;
	}

	public void setFallback(String fallback) {
		this.fallback = fallback;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getPretext() {
		return pretext;
	}

	public void setPretext(String pretext) {
		this.pretext = pretext;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorLink() {
		return authorLink;
	}

	public void setAuthorLink(String authorLink) {
		this.authorLink = authorLink;
	}

	public String getAuthorIcon() {
		return authorIcon;
	}

	public void setAuthorIcon(String authorIcon) {
		this.authorIcon = authorIcon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleLink() {
		return titleLink;
	}

	public void setTitleLink(String titleLink) {
		this.titleLink = titleLink;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

}

package com.charlesbot.slack;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Attachment {

	/**
	 * Required plain-text summary of the attachment.
	 */
	@JsonInclude(Include.NON_NULL)
	private String fallback;

	/**
	 * Good, warning, danger, or any hex color code
	 */
	@JsonInclude(Include.NON_NULL)
	private String color;

	/**
	 * Optional text that appears above the attachment block
	 */
	@JsonInclude(Include.NON_NULL)
	private String pretext;

	@JsonInclude(Include.NON_NULL)
	private String authorName;
	@JsonInclude(Include.NON_NULL)
	private String authorLink;
	@JsonInclude(Include.NON_NULL)
	private String authorIcon;

	/**
	 * Title of the attachment
	 */
	@JsonInclude(Include.NON_NULL)
	private String title;
	@JsonInclude(Include.NON_NULL)
	private String titleLink;

	/**
	 * Optional text that appears within the attachment
	 */
	@JsonInclude(Include.NON_NULL)
	private String text;
	@JsonInclude(Include.NON_NULL)
	private List<Field> fields;
	@JsonInclude(Include.NON_NULL)
	private String imageUrl;
	@JsonInclude(Include.NON_NULL)
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

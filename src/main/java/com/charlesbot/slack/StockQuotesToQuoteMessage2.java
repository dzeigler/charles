package com.charlesbot.slack;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.charlesbot.model.StockQuote;
import com.charlesbot.model.StockQuotes;

public class StockQuotesToQuoteMessage2 implements Converter<StockQuotes, QuoteMessage2> {

	@Override
	public QuoteMessage2 convert(StockQuotes stockQuotes) {
		QuoteMessage2 message = new QuoteMessage2();
		
		message.setAttachments(new ArrayList<Attachment>());
		for (StockQuote quote : stockQuotes.get()) {
			String color = determineColor(quote);
			String text = getText(quote);
			Attachment attachment = new Attachment();
			attachment.setColor(color);
			attachment.setFallback(text);
			attachment.setText(text);
			message.getAttachments().add(attachment);
		}
		
		return message;
	}

	private String getText(StockQuote quote) {
		StringBuffer sb = new StringBuffer();
		sb.append(MessageFormat.format("{0} ({4}): {1} {2} {3}%", quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getChangeInPercent(), quote.getName()));
		if (!StringUtils.isEmpty(quote.getExtendedHoursPrice())) {
			sb.append(MessageFormat.format(" extended hours: {5} {6} {7}%", quote.getExtendedHoursPrice(), quote.getExtendedHoursChange(), quote.getExtendedHoursChangeInPercent()));
		}
		return sb.toString();
	}

	private String determineColor(StockQuote quote) {
		double changeInPercent = 0d;
		try { 
			changeInPercent = new Double(quote.getChangeInPercent());
		} catch (Exception nfe) {
			// ignore
		}
		
		double extendedHoursChangeInPercent = 0d;
		try { 
			extendedHoursChangeInPercent = new Double(quote.getExtendedHoursChangeInPercent());
		} catch (Exception nfe) {
			// ignore
		}
		
		double totalChangeInPercent = changeInPercent + extendedHoursChangeInPercent;
		
		String color = "#000000";
		if (totalChangeInPercent <= -10) {
			color = "#ff0000";
		} else if (totalChangeInPercent <= -6) {
			color = "#cc0000";
		} else if (totalChangeInPercent <= -3) {
			color = "#990000";
		} else if (totalChangeInPercent <= -1) {
			color = "#660000";
		} else if (totalChangeInPercent < 0) {
			color = "#330000";
		} else if (totalChangeInPercent == 0) {
			color = "#000000";
		} else if (totalChangeInPercent <= 1) {
			color = "#003300";
		} else if (totalChangeInPercent <= 3) {
			color = "#006600";
		} else if (totalChangeInPercent <= 6) {
			color = "#006600";
		} else if (totalChangeInPercent < 10) {
			color = "#00cc00";
		} else {
			color = "#00ff00";
		}
		
		
		return color;
	}

}

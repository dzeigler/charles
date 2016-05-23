package com.charlesbot.slack;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.charlesbot.model.StockQuote;

public class StockQuoteToQuoteMessageTest {

	
	@Test
	public void totalPercentLessThanNeg10() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-11");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red5"));
	}
	
	@Test
	public void totalPercentEqualToNeg10() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-10");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red5"));
	}
	
	@Test
	public void totalPercentEqualToNeg9() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-9");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red4"));
	}
	
	@Test
	public void totalPercentEqualToNeg6() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-6");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red4"));
	}
	
	@Test
	public void totalPercentEqualToNeg4() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-4");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red3"));
	}
	
	@Test
	public void totalPercentEqualToNeg3() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-3");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red3"));
	}
	
	@Test
	public void totalPercentEqualToNeg2() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-2");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red2"));
	}
	
	@Test
	public void totalPercentEqualToNeg1() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-1");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red2"));
	}
	
	@Test
	public void totalPercentEqualToNegPoint5() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("-0.5");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("red1"));
	}
	
	@Test
	public void totalPercentEqualTo0() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("0");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("black"));
	}
	
	@Test
	public void totalPercentLessThan10() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("11");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green5"));
	}
	
	@Test
	public void totalPercentEqualTo10() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("10");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green5"));
	}
	
	@Test
	public void totalPercentEqualTo9() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("9");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green4"));
	}
	
	@Test
	public void totalPercentEqualTo6() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("6");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green4"));
	}
	
	@Test
	public void totalPercentEqualTo4() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("4");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green3"));
	}
	
	@Test
	public void totalPercentEqualTo3() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("3");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green3"));
	}
	
	@Test
	public void totalPercentEqualTo2() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("2");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green2"));
	}
	
	@Test
	public void totalPercentEqualTo1() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("1");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green2"));
	}
	
	@Test
	public void totalPercentEqualToPoint5() {
		
		StockQuote quote = new StockQuote();
		quote.setChangeInPercent("0.5");

		String emoji = new StockQuotesToQuoteMessage().determineRangeString(quote);
		
		assertThat(emoji, is("green1"));
	}
}

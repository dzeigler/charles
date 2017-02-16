package com.charlesbot;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.model.WatchListRepository;
import com.charlesbot.slack.listeners.MessageListener;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

@Configuration
public class SlackBotConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(SlackBotConfiguration.class);
	
	@Value("${SLACK_AUTH_TOKEN}")
	private String slackAuthToken;
	
	private SlackSession slackSession;
	
	@Bean
	public SlackSession slackSession(MessageListener messageListener) throws IOException {
		if (slackSession == null) {
			slackSession = SlackSessionFactory.createWebSocketSlackSession(slackAuthToken);
			slackSession.connect();
			slackSession.addMessagePostedListener(messageListener);
		}
		return slackSession;
	}
	
	@Bean
	public MessageListener messageListener(ConversionService conversionService,  WatchListRepository watchListRepository, CommandLineProcessor commandLineProcessor) {
		return new MessageListener(conversionService, watchListRepository, commandLineProcessor);
	}
	
	@PreDestroy
	public void disconnectSlackSession() throws IOException {
		if (slackSession != null && slackSession.isConnected()) {
			logger.info("Disconnecting from slack");
			slackSession.disconnect();
		}
	}
	
}

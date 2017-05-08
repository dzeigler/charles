package com.charlesbot.slack.listeners;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;

import com.charlesbot.cli.Command;
import com.charlesbot.cli.CommandLineProcessor;
import com.charlesbot.model.WatchListRepository;
import com.google.common.collect.Lists;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackPersona;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

public class MessageListener implements SlackMessagePostedListener {

	private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
	private List<String> keywords;
	private ConversionService conversionService;
	private CommandLineProcessor commandLineProcessor;
	
	MessageListener() {
		keywords = Lists.newArrayList("!q", "!stats", "!chart", "!pf");
	}
	
	public MessageListener(ConversionService conversionService, WatchListRepository repo, CommandLineProcessor commandLineProcessor) {
		this();
		this.conversionService = conversionService;
		this.commandLineProcessor = commandLineProcessor;
	}
	
	@Override
	public void onEvent(SlackMessagePosted event, SlackSession session) {
		try {
			String messageContent = event.getMessageContent();
			SlackPersona sessionPersona = session.sessionPersona();
			String userId = sessionPersona.getId();
			String userIdMention = "<@"+userId+">";
			String userNameMention = "@"+sessionPersona.getUserName();
			if (isCharlesCommand(messageContent, userIdMention) && !event.getSender().getId().equals(userId)) {
				SlackChannel channel = event.getChannel();
				
				String sanitizedCommandString = event.getMessageContent().replace(userIdMention, userNameMention);
				
				Command command = commandLineProcessor.process(sanitizedCommandString, event.getSender().getId(), sessionPersona.getUserName());
				@SuppressWarnings("unchecked")
				List<String> replies = conversionService.convert(command, List.class);
				
				for (String reply : replies) {
					if (StringUtils.isNotBlank(reply)) {
						session.sendMessage(channel, reply);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception while processing the string {}", event.getMessageContent(), e);
			throw e;
		}
	}
	
	private boolean isCharlesCommand(String command, String userIdMention) {
		
		for (String word : keywords) {
			if (command.toLowerCase().startsWith(word.toLowerCase())) {
				return true;
			}
		}
		
		if (command.startsWith(userIdMention)) {
			return true;
		}
		return false;
	}
	
}

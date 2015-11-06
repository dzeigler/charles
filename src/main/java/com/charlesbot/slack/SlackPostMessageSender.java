package com.charlesbot.slack;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Named
public class SlackPostMessageSender {

	private static final Logger log = LoggerFactory.getLogger(SlackPostMessageSender.class);
	
	@Inject
	private RestTemplate restTemplate;
	@Value("${WEBHOOK_URL}")
	private String webhookUrl;
	
	public void send(SlackIncomingMessage message) {
		ResponseEntity<SlackIncomingMessageResponse> response = restTemplate.postForEntity(webhookUrl, message, null);
		if (response.hasBody()) {
			log.error("Message was not posted. message={}", message);
		}
	}
	
}

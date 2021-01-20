package com.izeye.application.bithumbautotrader.support.messaging.service;

import org.springframework.stereotype.Service;

import com.izeye.application.bithumbautotrader.support.slack.service.SlackService;

/**
 * {@link MessagingService} backed by Slack.
 *
 * @author Johnny Lim
 */
@Service
public class SlackMessagingService implements MessagingService {

	private final SlackService slackService;

	public SlackMessagingService(SlackService slackService) {
		this.slackService = slackService;
	}

	@Override
	public void sendMessage(String message) {
		this.slackService.sendMessage(message);
	}

}

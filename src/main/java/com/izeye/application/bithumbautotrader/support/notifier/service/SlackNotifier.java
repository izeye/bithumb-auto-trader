package com.izeye.application.bithumbautotrader.support.notifier.service;

import org.springframework.stereotype.Service;

import com.izeye.application.bithumbautotrader.support.notifier.domain.Notification;
import com.izeye.application.bithumbautotrader.support.slack.service.SlackService;

/**
 * Slack-based {@link Notifier}.
 *
 * @author Johnny Lim
 */
@Service
public class SlackNotifier implements Notifier {

	private final SlackService slackService;

	public SlackNotifier(SlackService slackService) {
		this.slackService = slackService;
	}

	@Override
	public void notify(Notification notification) {
		this.slackService.sendMessage("Message: " + notification.getMessage());
		this.slackService.uploadFile("Detail", notification.getDetail());
	}

}

package com.izeye.application.bithumbautotrader.support.notifier.service;

import org.springframework.stereotype.Service;

import com.izeye.application.bithumbautotrader.support.messaging.service.MessagingService;
import com.izeye.application.bithumbautotrader.support.notifier.domain.Notification;

/**
 * Slack-based {@link Notifier}.
 *
 * @author Johnny Lim
 */
@Service
public class SlackNotifier implements Notifier {

	private final MessagingService messagingService;

	public SlackNotifier(MessagingService messagingService) {
		this.messagingService = messagingService;
	}

	@Override
	public void notify(Notification notification) {
		String message = String.format("Message: %s\nDetail:\n%s", notification.getMessage(), notification.getDetail());
		this.messagingService.sendMessage(message);
	}

}

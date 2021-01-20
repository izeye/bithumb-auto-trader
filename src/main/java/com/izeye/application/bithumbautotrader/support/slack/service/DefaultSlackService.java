package com.izeye.application.bithumbautotrader.support.slack.service;

import java.io.IOException;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import org.springframework.stereotype.Service;

/**
 * Default {@link SlackService}.
 *
 * @author Johnny Lim
 */
@Service
public class DefaultSlackService implements SlackService {

	private final SlackProperties properties;

	private final MethodsClient client;

	public DefaultSlackService(SlackProperties properties) {
		this.properties = properties;

		this.client = Slack.getInstance().methods(properties.getToken());
	}

	@Override
	public void sendMessage(String message) {
		ChatPostMessageRequest request = ChatPostMessageRequest.builder().channel(this.properties.getChannel())
				.text(message).build();
		try {
			this.client.chatPostMessage(request);
		}
		catch (IOException | SlackApiException ex) {
			throw new RuntimeException(ex);
		}
	}

}

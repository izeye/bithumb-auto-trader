package com.izeye.application.bithumbautotrader.support.slack.service;

import java.io.IOException;
import java.util.Arrays;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.files.FilesUploadResponse;
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
	public boolean sendMessage(String message) {
		ChatPostMessageRequest request = ChatPostMessageRequest.builder().channel(this.properties.getChannel())
				.text(message).build();
		try {
			ChatPostMessageResponse response = this.client.chatPostMessage(request);
			return response.isOk();
		}
		catch (IOException | SlackApiException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean uploadFile(String title, String content) {
		FilesUploadRequest request = FilesUploadRequest.builder().channels(Arrays.asList(this.properties.getChannel()))
				.title(title).content(content).build();
		try {
			FilesUploadResponse response = this.client.filesUpload(request);
			return response.isOk();
		}
		catch (IOException | SlackApiException ex) {
			throw new RuntimeException(ex);
		}
	}

}

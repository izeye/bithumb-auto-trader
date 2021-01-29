package com.izeye.application.bithumbautotrader.support.slack.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SlackService}.
 *
 * @author Johnny Lim
 */
@SpringBootTest
class SlackServiceTests {

	@Autowired
	private SlackService slackService;

	@Disabled("This will trigger a message.")
	@Test
	void sendMessage() {
		assertThat(this.slackService.sendMessage("Hello, world!")).isTrue();
	}

	@Disabled("This will trigger a file upload.")
	@Test
	void uploadFile() {
		assertThat(this.slackService.uploadFile("Greeting", "Hello, world!")).isTrue();
	}

}

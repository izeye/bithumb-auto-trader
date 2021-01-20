package com.izeye.application.bithumbautotrader.support.slack.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
		this.slackService.sendMessage("Hello, world!");
	}

}

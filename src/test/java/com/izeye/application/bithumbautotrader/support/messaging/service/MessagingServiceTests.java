package com.izeye.application.bithumbautotrader.support.messaging.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for {@link MessagingService}.
 *
 * @author Johnny Lim
 */
@SpringBootTest
class MessagingServiceTests {

	@Autowired
	private MessagingService messagingService;

	@Disabled("This will send a message.")
	@Test
	void sendMessage() {
		this.messagingService.sendMessage("Hello, coin!");
	}

}

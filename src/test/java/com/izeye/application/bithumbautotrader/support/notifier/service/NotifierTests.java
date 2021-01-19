package com.izeye.application.bithumbautotrader.support.notifier.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.izeye.application.bithumbautotrader.support.notifier.domain.Notification;

/**
 * Tests for {@link Notifier}.
 *
 * @author Johnny Lim
 */
@SpringBootTest
class NotifierTests {

	@Autowired
	private Notifier notifier;

	@Disabled("This will trigger a notification.")
	@Test
	void test() {
		try {
			throw new RuntimeException("Error.");
		}
		catch (RuntimeException ex) {
			Notification notification = new Notification(ex);
			this.notifier.notify(notification);
		}
	}

}

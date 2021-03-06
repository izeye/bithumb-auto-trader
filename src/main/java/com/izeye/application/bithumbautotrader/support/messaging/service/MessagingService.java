package com.izeye.application.bithumbautotrader.support.messaging.service;

/**
 * Service for messaging.
 *
 * @author Johnny Lim
 */
public interface MessagingService {

	void sendMessage(String message);

	default void sendMessage(Object message) {
		sendMessage(message.toString());
	}

}

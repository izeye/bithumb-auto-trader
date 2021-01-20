package com.izeye.application.bithumbautotrader.support.slack.service;

/**
 * Service interface for Slack.
 *
 * @author Johnny Lim
 */
public interface SlackService {

	boolean sendMessage(String message);

	boolean uploadFile(String filename, String content);

}

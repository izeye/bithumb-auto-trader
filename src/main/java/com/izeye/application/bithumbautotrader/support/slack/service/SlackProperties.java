package com.izeye.application.bithumbautotrader.support.slack.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * {@link ConfigurationProperties} for Slack.
 *
 * @author Johnny Lim
 */
@ConfigurationProperties("slack")
@ConstructorBinding
@Data
public class SlackProperties {

	private final String token;

	private final String channel;

}

package com.izeye.application.bithumbautotrader.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * {@link ConfigurationProperties} for Bithumb.
 *
 * @author Johnny Lim
 */
@ConfigurationProperties("bithumb")
@ConstructorBinding
@Data
public class BithumbProperties {

	private final String connectKey;

	private final String secretKey;

	private final double tradingFeeInPercentages;

	public BithumbProperties(String connectKey, String secretKey,
			@DefaultValue("0.25") double tradingFeeInPercentages) {
		this.connectKey = connectKey;
		this.secretKey = secretKey;
		this.tradingFeeInPercentages = tradingFeeInPercentages;
	}

}

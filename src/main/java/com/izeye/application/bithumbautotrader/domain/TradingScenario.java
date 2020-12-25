package com.izeye.application.bithumbautotrader.domain;

import lombok.Data;

/**
 * Trading scenario.
 *
 * @author Johnny Lim
 */
@Data
public class TradingScenario {

	private final Currency currency;

	private final double currencyUnit;

	private final double signalGapInPercentages;

}

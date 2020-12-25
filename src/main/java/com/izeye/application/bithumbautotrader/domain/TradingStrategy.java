package com.izeye.application.bithumbautotrader.domain;

import lombok.Data;

/**
 * Trading strategy.
 *
 * @author Johnny Lim
 */
@Data
public class TradingStrategy {

	private final double buySignalGapInPercentages;

	private final double sellSignalGapInPercentages;

	public TradingStrategy(double signalGapInPercentages) {
		this.buySignalGapInPercentages = -signalGapInPercentages;
		this.sellSignalGapInPercentages = signalGapInPercentages;
	}

}

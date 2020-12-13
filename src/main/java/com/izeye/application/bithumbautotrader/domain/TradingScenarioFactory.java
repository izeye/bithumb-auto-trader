package com.izeye.application.bithumbautotrader.domain;

/**
 * Factory for {@link TradingScenario}.
 *
 * @author Johnny Lim
 */
public final class TradingScenarioFactory {

	public static TradingScenario[] createLinearScenarios(Currency currency, int start, int end) {
		TradingScenario[] scenarios = new TradingScenario[end - start + 1];
		for (int i = start; i <= end; i++) {
			scenarios[i - start] = new TradingScenario(currency, i * 10, i);
		}
		return scenarios;
	}

	private TradingScenarioFactory() {
	}

}

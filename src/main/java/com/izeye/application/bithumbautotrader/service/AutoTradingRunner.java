package com.izeye.application.bithumbautotrader.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.TradingScenario;
import com.izeye.application.bithumbautotrader.domain.TradingScenarioFactory;

/**
 * {@link ApplicationRunner} for auto-trading.
 *
 * @author Johnny Lim
 */
@Component
public class AutoTradingRunner implements ApplicationRunner {

	private final AutoTradingService autoTradingService;

	public AutoTradingRunner(AutoTradingService autoTradingService) {
		this.autoTradingService = autoTradingService;
	}

	@Override
	public void run(ApplicationArguments args) {
		TradingScenario[] scenarios = TradingScenarioFactory.createLinearScenarios(Currency.XRP, 1, 10);
		this.autoTradingService.start(scenarios);
	}

}

package com.izeye.application.bithumbautotrader.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AutoTradingRunner implements ApplicationRunner {

	private static final int MAX_EXPECTED_XRP_PRICE = 2_000;

	private final AutoTradingService autoTradingService;

	private final BithumbApiService bithumbApiService;

	private final BithumbProperties bithumbProperties;

	public AutoTradingRunner(AutoTradingService autoTradingService, BithumbApiService bithumbApiService,
			BithumbProperties bithumbProperties) {
		this.autoTradingService = autoTradingService;
		this.bithumbApiService = bithumbApiService;
		this.bithumbProperties = bithumbProperties;
	}

	@Override
	public void run(ApplicationArguments args) {
		Currency currency = Currency.XRP;

		double currentPrice = this.bithumbApiService.getOrderBook(currency, Currency.KRW).block().getHighestBid();
		log.info("Current price for {}: {}", currency, currentPrice);

		int numberOfScenarios = (int) (MAX_EXPECTED_XRP_PRICE / currentPrice);
		log.info("Number of scenarios: {}", numberOfScenarios);

		TradingScenario[] scenarios = TradingScenarioFactory.createLinearScenarios(currency, 1, numberOfScenarios,
				this.bithumbProperties.getTradingFeeInPercentages());
		this.autoTradingService.start(scenarios);
	}

}

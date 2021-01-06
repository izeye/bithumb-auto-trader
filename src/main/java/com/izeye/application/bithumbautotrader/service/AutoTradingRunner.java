package com.izeye.application.bithumbautotrader.service;

import java.util.stream.Stream;

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

	private static final int MAX_EXPECTED_ADA_PRICE = 2_000;

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
		TradingScenario[] xrpScenarios = createScenarios(Currency.XRP, MAX_EXPECTED_XRP_PRICE);
		TradingScenario[] adaScenarios = createScenarios(Currency.ADA, MAX_EXPECTED_ADA_PRICE);
		TradingScenario[] tradingScenarios = Stream.of(xrpScenarios, adaScenarios).flatMap(Stream::of)
				.toArray(TradingScenario[]::new);
		this.autoTradingService.start(tradingScenarios);
	}

	private TradingScenario[] createScenarios(Currency currency, int maxExpectedPrice) {
		double currentPrice = this.bithumbApiService.getOrderBook(currency, Currency.KRW).block().getHighestBid();
		log.info("Current price for {}: {}", currency, currentPrice);

		int numberOfScenarios = (int) (maxExpectedPrice / currentPrice);
		log.info("Number of scenarios: {}", numberOfScenarios);

		TradingScenario[] scenarios = TradingScenarioFactory.createLinearScenarios(currency, 1, numberOfScenarios,
				this.bithumbProperties.getTradingFeeInPercentages());
		return scenarios;
	}

}

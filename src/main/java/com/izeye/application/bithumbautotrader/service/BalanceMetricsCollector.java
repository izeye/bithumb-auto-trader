package com.izeye.application.bithumbautotrader.service;

import java.util.Map;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.OrderBook;

/**
 * {@link ApplicationRunner} for collecting balance metrics.
 *
 * @author Johnny Lim
 */
@Component
public class BalanceMetricsCollector implements ApplicationRunner {

	private final BithumbApiService bithumbApiService;

	private final MeterRegistry meterRegistry;

	public BalanceMetricsCollector(BithumbApiService bithumbApiService, MeterRegistry meterRegistry) {
		this.bithumbApiService = bithumbApiService;
		this.meterRegistry = meterRegistry;
	}

	@Override
	public void run(ApplicationArguments args) {
		Currency currency = Currency.XRP;

		// FIXME:
		// - Apply cache to BithumbApiService.getBalance().
		// - Use non-blocking suppliers for gauges in BalanceMetricsCollector.
		Gauge.builder("balance", () -> this.bithumbApiService.getBalance(currency).get(Currency.KRW))
				.tag("currency", currency.KRW.name()).register(this.meterRegistry);
		Gauge.builder("balance", () -> this.bithumbApiService.getBalance(currency).get(currency))
				.tag("currency", currency.name()).register(this.meterRegistry);
		Gauge.builder("balance.total", () -> getTotalBalance(currency)).register(this.meterRegistry);
	}

	private double getTotalBalance(Currency currency) {
		Map<Currency, Double> balance = this.bithumbApiService.getBalance(currency);

		Double krwUnits = balance.get(Currency.KRW);
		Double currencyUnits = balance.get(currency);
		OrderBook orderBook = this.bithumbApiService.getOrderBook(currency, Currency.KRW).block();
		return krwUnits + orderBook.getHighestBid() * currencyUnits;
	}

}

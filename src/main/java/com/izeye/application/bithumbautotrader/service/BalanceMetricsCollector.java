package com.izeye.application.bithumbautotrader.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		Set<Currency> currencies = new HashSet<>(
				Arrays.asList(Currency.XRP, Currency.ADA, Currency.XTZ, Currency.BTC, Currency.ETH));

		// FIXME:
		// - Apply cache to BithumbApiService.getBalance().
		// - Use non-blocking suppliers for gauges in BalanceMetricsCollector.
		Gauge.builder("balance", () -> this.bithumbApiService.getBalance(currencies).get(Currency.KRW))
				.tag("currency", Currency.KRW.name()).register(this.meterRegistry);

		for (Currency currency : currencies) {
			Gauge.builder("balance", () -> this.bithumbApiService.getBalance(currency).get(currency))
					.tag("currency", currency.name()).register(this.meterRegistry);
		}

		Gauge.builder("balance.total", () -> getTotalBalance(currencies)).register(this.meterRegistry);
	}

	private double getTotalBalance(Set<Currency> currencies) {
		Map<Currency, Double> balance = this.bithumbApiService.getBalance(currencies);

		double totalBalance = balance.get(Currency.KRW);
		for (Currency currency : currencies) {
			Double currencyUnits = balance.get(currency);
			OrderBook orderBook = this.bithumbApiService.getOrderBook(currency, Currency.KRW).block();
			totalBalance += orderBook.getHighestBid() * currencyUnits;
		}
		return totalBalance;
	}

}

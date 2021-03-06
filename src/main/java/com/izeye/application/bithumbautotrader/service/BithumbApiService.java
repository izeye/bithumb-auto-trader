package com.izeye.application.bithumbautotrader.service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.OrderBook;
import com.izeye.application.bithumbautotrader.domain.TradePlaceRequest;

/**
 * Service for Bithumb API.
 *
 * @author Johnny Lim
 */
public interface BithumbApiService {

	Mono<OrderBook> getOrderBook(Currency cryptocurrency, Currency fiatCurrency);

	Flux<OrderBook> getOrderBooks(Set<Currency> cryptocurrencies, Currency fiatCurrency);

	default Map<Currency, Double> getBalance(Currency currency) {
		return getBalance(Collections.singleton(currency));
	}

	Map<Currency, Double> getBalance(Set<Currency> currencies);

	String tradePlace(TradePlaceRequest request);

}

package com.izeye.application.bithumbautotrader.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.izeye.application.bithumbautotrader.domain.CryptocurrencyExchange;
import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.OrderBook;
import com.izeye.application.bithumbautotrader.domain.TradePlaceRequest;
import com.izeye.application.bithumbautotrader.domain.TradePlaceType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BithumbApiService}.
 *
 * @author Johnny Lim
 */
@SpringBootTest
class BithumbApiServiceTests {

	@Autowired
	private BithumbApiService bithumbApiService;

	@Test
	void getOrderBook() {
		OrderBook orderBook = this.bithumbApiService.getOrderBook(Currency.XRP, Currency.KRW).block();
		double highestBid = orderBook.getHighestBid();
		assertThat(highestBid).isGreaterThan(0);
		assertThat(orderBook.getLowestAsk()).isGreaterThan(highestBid);
	}

	@Test
	void getOrderBooks() {
		Set<Currency> cryptocurrencies = CryptocurrencyExchange.BITHUMB.getSupportedCryptocurrencies();
		List<OrderBook> orderBooks = this.bithumbApiService.getOrderBooks(cryptocurrencies, Currency.KRW).collectList()
				.block();
		for (OrderBook orderBook : orderBooks) {
			double highestBid = orderBook.getHighestBid();
			assertThat(highestBid).isGreaterThan(0);
			assertThat(orderBook.getLowestAsk()).isGreaterThan(highestBid);
		}
	}

	@Disabled
	@Test
	void tradePlaceBid() {
		Currency orderCurrency = Currency.XRP;
		Currency paymentCurrency = Currency.KRW;
		int units = 1;
		int price = 500;
		TradePlaceType type = TradePlaceType.BID;

		TradePlaceRequest request = new TradePlaceRequest(orderCurrency, paymentCurrency, units, price, type);
		String orderId = this.bithumbApiService.tradePlace(request);
		assertThat(orderId).isNotNull();
	}

	@Disabled
	@Test
	void tradePlaceAsk() {
		Currency orderCurrency = Currency.XRP;
		Currency paymentCurrency = Currency.KRW;
		int units = 1;
		int price = 600;
		TradePlaceType type = TradePlaceType.ASK;

		TradePlaceRequest request = new TradePlaceRequest(orderCurrency, paymentCurrency, units, price, type);
		String orderId = this.bithumbApiService.tradePlace(request);
		assertThat(orderId).isNotNull();
	}

	@Test
	void getBalance() {
		Currency currency = Currency.XRP;

		Map<Currency, Double> balance = this.bithumbApiService.getBalance(currency);
		System.out.println(balance);

		assertThat(balance.get(Currency.KRW)).isGreaterThan(0);
		assertThat(balance.get(currency)).isGreaterThan(0);
	}

	@Test
	void getBalanceSetOfCurrencies() {
		Set<Currency> currencies = new HashSet<>(Arrays.asList(Currency.XRP, Currency.ADA, Currency.XTZ));

		Map<Currency, Double> balance = this.bithumbApiService.getBalance(currencies);
		System.out.println(balance);

		assertThat(balance.get(Currency.KRW)).isGreaterThan(0);
		for (Currency currency : currencies) {
			assertThat(balance.get(currency)).isGreaterThan(0);
		}
	}

}

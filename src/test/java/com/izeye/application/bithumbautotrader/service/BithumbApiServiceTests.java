package com.izeye.application.bithumbautotrader.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.izeye.application.bithumbautotrader.domain.Currency;
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

}

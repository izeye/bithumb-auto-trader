package com.izeye.application.bithumbautotrader.service;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.TradingScenario;

/**
 * Tests for {@link AutoTradingService}.
 *
 * @author Johnny Lim
 */
@SpringBootTest
class AutoTradingServiceTests {

	@Autowired
	private AutoTradingService autoTradingService;

	@Test
	void runScenarios() {
		startAutoTradingServiceStopThread();

		this.autoTradingService.start(new TradingScenario(Currency.XRP, 1, 1));
	}

	private void startAutoTradingServiceStopThread() {
		new Thread(() -> {
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(10));
				this.autoTradingService.stop();
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}).start();
	}

}

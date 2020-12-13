package com.izeye.application.bithumbautotrader.web;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.TradingScenarioFactory;
import com.izeye.application.bithumbautotrader.service.AutoTradingService;

/**
 * {@link RestController} for auto-trading.
 *
 * @author Johnny Lim
 */
@RestController
@RequestMapping(path = "/auto-trading")
public class AutoTradingApiController {

	private final AutoTradingService autoTradingService;

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private volatile Future<?> future;

	public AutoTradingApiController(AutoTradingService autoTradingService) {
		this.autoTradingService = autoTradingService;
	}

	@PostMapping("/start")
	public String start() {
		if (this.future != null) {
			return "Already running.";
		}

		this.future = this.executorService.submit(
				() -> this.autoTradingService.start(TradingScenarioFactory.createLinearScenarios(Currency.XRP, 1, 10)));
		return "Started.";
	}

	@PostMapping("/stop")
	public String stop() throws ExecutionException, InterruptedException {
		if (this.future == null) {
			return "Already stopped.";
		}

		this.autoTradingService.stop();
		this.future.get();
		this.future = null;
		return "Stopped.";
	}

}

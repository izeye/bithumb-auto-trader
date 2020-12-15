package com.izeye.application.bithumbautotrader.web;

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

	public AutoTradingApiController(AutoTradingService autoTradingService) {
		this.autoTradingService = autoTradingService;
	}

	@PostMapping("/start")
	public String start() {
		boolean started = this.autoTradingService
				.start(TradingScenarioFactory.createLinearScenarios(Currency.XRP, 1, 10));
		return started ? "Started." : "Already running.";
	}

	@PostMapping("/stop")
	public String stop() {
		boolean stopped = this.autoTradingService.stop();
		return stopped ? "Stopped." : "Already stopped.";
	}

}

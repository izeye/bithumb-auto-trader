package com.izeye.application.bithumbautotrader.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.TradingScenario;
import com.izeye.application.bithumbautotrader.domain.TradingScenarioFactory;
import com.izeye.application.bithumbautotrader.service.AutoTradingService;
import com.izeye.application.bithumbautotrader.service.BithumbProperties;

/**
 * {@link RestController} for auto-trading.
 *
 * @author Johnny Lim
 */
@RestController
@RequestMapping(path = "/auto-trading")
public class AutoTradingApiController {

	private final AutoTradingService autoTradingService;

	private final BithumbProperties bithumbProperties;

	public AutoTradingApiController(AutoTradingService autoTradingService, BithumbProperties bithumbProperties) {
		this.autoTradingService = autoTradingService;
		this.bithumbProperties = bithumbProperties;
	}

	@PostMapping("/start")
	public String start() {
		TradingScenario[] scenarios = TradingScenarioFactory.createLinearScenarios(Currency.XRP, 1, 10,
				this.bithumbProperties.getTradingFeeInPercentages());
		boolean started = this.autoTradingService.start(scenarios);
		return started ? "Started." : "Already running.";
	}

	@PostMapping("/stop")
	public String stop() {
		boolean stopped = this.autoTradingService.stop();
		return stopped ? "Stopped." : "Already stopped.";
	}

}

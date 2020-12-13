package com.izeye.application.bithumbautotrader.service;

import com.izeye.application.bithumbautotrader.domain.TradingScenario;

/**
 * Service for auto-trading.
 *
 * @author Johnny Lim
 */
public interface AutoTradingService {

	void start(TradingScenario... scenarios);

	void stop();

}

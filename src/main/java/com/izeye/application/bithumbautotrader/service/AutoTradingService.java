package com.izeye.application.bithumbautotrader.service;

import com.izeye.application.bithumbautotrader.domain.TradingScenario;

/**
 * Service for auto-trading.
 *
 * @author Johnny Lim
 */
public interface AutoTradingService {

	boolean start(TradingScenario... scenarios);

	boolean stop();

}

package com.izeye.application.bithumbautotrader.service;

import com.izeye.application.bithumbautotrader.domain.TradePlaceRequest;

/**
 * Service for Bithumb API.
 *
 * @author Johnny Lim
 */
public interface BithumbApiService {

	String tradePlace(TradePlaceRequest request);

}

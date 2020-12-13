package com.izeye.application.bithumbautotrader.domain;

import lombok.Data;

/**
 * Order book.
 *
 * @author Johnny Lim
 */
@Data
public class OrderBook {

	private CryptocurrencyExchange exchange;

	private Currency targetCurrency;

	private Currency baseCurrency;

	private double highestBid;

	private double lowestAsk;

}

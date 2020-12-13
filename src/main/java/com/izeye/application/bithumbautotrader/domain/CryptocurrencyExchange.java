package com.izeye.application.bithumbautotrader.domain;

import java.util.Set;

/**
 * Exchange for cryptocurrencies.
 *
 * @author Johnny Lim
 */
public enum CryptocurrencyExchange {

	/**
	 * For Bithumb from South Korea.
	 */
	BITHUMB(Currency.getCurrencies(CurrencyType.CRYPTO)),

	/**
	 * For Kraken from United States.
	 */
	KRAKEN(Currency.getCurrencies(CurrencyType.CRYPTO)),

	/**
	 * For GMO Coin from Japan.
	 */
	GMO_COIN(Set.of(Currency.BTC, Currency.ETH, Currency.XRP));

	private final Set<Currency> supportedCryptocurrencies;

	CryptocurrencyExchange(Set<Currency> supportedCryptocurrencies) {
		this.supportedCryptocurrencies = supportedCryptocurrencies;
	}

	public Set<Currency> getSupportedCryptocurrencies() {
		return this.supportedCryptocurrencies;
	}

	public boolean supports(Currency cryptocurrency) {
		return this.supportedCryptocurrencies.contains(cryptocurrency);
	}

}

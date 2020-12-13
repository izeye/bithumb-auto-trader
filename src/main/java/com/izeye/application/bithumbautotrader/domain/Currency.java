package com.izeye.application.bithumbautotrader.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Currency.
 *
 * @author Johnny Lim
 */
public enum Currency {

	/**
	 * For South Korean won.
	 */
	KRW(CurrencyType.FIAT),

	/**
	 * For United States dollar.
	 */
	USD(CurrencyType.FIAT),

	/**
	 * For Japanese yen.
	 */
	JPY(CurrencyType.FIAT),

	/**
	 * For Bitcoin.
	 */
	BTC(CurrencyType.CRYPTO),

	/**
	 * For Ethereum.
	 */
	ETH(CurrencyType.CRYPTO),

	/**
	 * For Chainlink.
	 */
	LINK(CurrencyType.CRYPTO),

	/**
	 * For Ripple.
	 */
	XRP(CurrencyType.CRYPTO),

	/**
	 * For Tezos.
	 */
	XTZ(CurrencyType.CRYPTO);

	private static final Map<CurrencyType, Set<Currency>> CURRENCIES_BY_TYPE;

	static {
		Map<CurrencyType, Set<Currency>> currenciesByType = new HashMap<>();
		for (Currency currency : values()) {
			currenciesByType.computeIfAbsent(currency.getType(), (key) -> new HashSet<>()).add(currency);
		}
		CURRENCIES_BY_TYPE = currenciesByType;
	}

	private final CurrencyType type;

	Currency(CurrencyType type) {
		this.type = type;
	}

	public CurrencyType getType() {
		return this.type;
	}

	public static Set<Currency> getCurrencies(CurrencyType type) {
		return CURRENCIES_BY_TYPE.get(type);
	}

	public static Set<Currency> getCurrenciesExcept(CurrencyType type, Currency... exceptedCurrencies) {
		Set<Currency> copied = new HashSet<>(CURRENCIES_BY_TYPE.get(type));
		copied.removeAll(Set.of(exceptedCurrencies));
		return copied;
	}

}

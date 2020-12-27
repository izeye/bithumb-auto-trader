package com.izeye.application.bithumbautotrader.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.OrderBook;
import com.izeye.application.bithumbautotrader.domain.TradePlaceRequest;
import com.izeye.application.bithumbautotrader.domain.TradePlaceType;
import com.izeye.application.bithumbautotrader.domain.TradingScenario;
import com.izeye.application.bithumbautotrader.domain.TradingScenarioExecution;
import com.izeye.application.bithumbautotrader.domain.TradingStrategy;
import com.izeye.application.bithumbautotrader.support.messaging.service.SlackMessagingService;

/**
 * Default {@link AutoTradingService}.
 *
 * @author Johnny Lim
 */
@Service
@Slf4j
public class DefaultAutoTradingService implements AutoTradingService {

	private final BithumbApiService bithumbApiService;

	private final BithumbProperties bithumbProperties;

	private final SlackMessagingService slackMessagingService;

	private final Map<Currency, OrderBook> orderBookCache = new HashMap<>();

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private final Counter totalTradingCurrency;

	private volatile Future<?> future;

	private volatile boolean running;

	public DefaultAutoTradingService(BithumbApiService bithumbApiService, BithumbProperties bithumbProperties,
			SlackMessagingService slackMessagingService, MeterRegistry meterRegistry) {
		this.bithumbApiService = bithumbApiService;
		this.bithumbProperties = bithumbProperties;
		this.slackMessagingService = slackMessagingService;
		this.totalTradingCurrency = meterRegistry.counter("trading.currency");
	}

	@Override
	public boolean start(TradingScenario... scenarios) {
		if (this.future != null) {
			return false;
		}

		this.future = this.executorService.submit(() -> doStart(scenarios));
		return true;
	}

	private void doStart(TradingScenario[] scenarios) {
		log.info("Auto-trading started.");
		this.running = true;

		TradingScenarioExecution[] executions = createTradingScenarioExecutions(scenarios);
		while (this.running) {
			for (TradingScenarioExecution execution : executions) {
				runExecution(execution);
			}
			this.orderBookCache.clear();

			sleep();
		}
	}

	private TradingScenarioExecution[] createTradingScenarioExecutions(TradingScenario[] scenarios) {
		TradingScenarioExecution[] executions = new TradingScenarioExecution[scenarios.length];
		for (int i = 0; i < executions.length; i++) {
			TradingScenario scenario = scenarios[i];
			executions[i] = new TradingScenarioExecution(scenario, this.bithumbProperties.getTradingFeeInPercentages());
			executions[i].setBasePrice(getCurrentBasePrice(scenario.getCurrency()));
			executions[i].logPrices();
		}
		return executions;
	}

	private double getCurrentBasePrice(Currency currency) {
		OrderBook orderBook = getOrderBook(currency);
		return (orderBook.getHighestBid() + orderBook.getLowestAsk()) / 2;
	}

	private void runExecution(TradingScenarioExecution execution) {
		try {
			TradingScenario scenario = execution.getScenario();
			Currency currency = scenario.getCurrency();
			OrderBook orderBook = getOrderBook(currency);

			double highestBuyPrice = orderBook.getHighestBid();
			double lowestSellPrice = orderBook.getLowestAsk();

			double basePrice = execution.getBasePrice();
			double buyPriceGapInPercentages = calculateGapInPercentages(basePrice, lowestSellPrice);
			double sellPriceGapInPercentages = calculateGapInPercentages(basePrice, highestBuyPrice);

			TradingStrategy strategy = execution.getStrategy();
			double currencyUnit = scenario.getCurrencyUnit();
			if (buyPriceGapInPercentages <= strategy.getBuySignalGapInPercentages()) {
				log.info("basePrice: {}", basePrice);
				log.info("buyPriceGapInPercentages: {}", buyPriceGapInPercentages);
				log.info("Try to buy now: {}", lowestSellPrice);

				TradePlaceRequest request = new TradePlaceRequest(currency, Currency.KRW, currencyUnit, lowestSellPrice,
						TradePlaceType.BID);
				this.bithumbApiService.tradePlace(request);
				this.slackMessagingService.sendMessage(request);

				// FIXME: This should be replaced with the actual buy price.
				double buyPrice = lowestSellPrice;
				execution.buy(buyPrice);
				execution.logPrices();
				execution.logTotalStatistics();

				this.totalTradingCurrency.increment(buyPrice * currencyUnit);
			}
			else if (sellPriceGapInPercentages >= strategy.getSellSignalGapInPercentages()) {
				log.info("basePrice: {}", basePrice);
				log.info("sellPriceGapInPercentages: {}", sellPriceGapInPercentages);
				log.info("Try to sell now: {}", highestBuyPrice);

				TradePlaceRequest request = new TradePlaceRequest(currency, Currency.KRW, currencyUnit, highestBuyPrice,
						TradePlaceType.ASK);
				this.bithumbApiService.tradePlace(request);
				this.slackMessagingService.sendMessage(request);

				// FIXME: This should be replaced with the actual sell price.
				double sellPrice = highestBuyPrice;
				execution.sell(sellPrice);
				execution.logPrices();
				execution.logTotalStatistics();

				this.totalTradingCurrency.increment(sellPrice * currencyUnit);
			}
		}
		catch (RuntimeException ex) {
			log.error("Unexpected error.", ex);
		}
	}

	private OrderBook getOrderBook(Currency currency) {
		OrderBook orderBook = this.orderBookCache.get(currency);
		if (orderBook == null) {
			orderBook = this.bithumbApiService.getOrderBook(currency, Currency.KRW).block();
			this.orderBookCache.put(currency, orderBook);
		}
		return orderBook;
	}

	private double calculateGapInPercentages(double baseValue, double value) {
		return (value - baseValue) * 100 / baseValue;
	}

	private void sleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		}
		catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean stop() {
		if (this.future == null) {
			return false;
		}

		doStop();

		try {
			this.future.get();
			log.info("Auto-trading stopped.");
		}
		catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		catch (ExecutionException ex) {
			throw new RuntimeException(ex);
		}
		this.future = null;
		return true;
	}

	private void doStop() {
		this.running = false;
	}

}

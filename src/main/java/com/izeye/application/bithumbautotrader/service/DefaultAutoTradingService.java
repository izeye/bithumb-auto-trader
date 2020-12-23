package com.izeye.application.bithumbautotrader.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

	private final SlackMessagingService slackMessagingService;

	private final Map<Currency, OrderBook> orderBookCache = new HashMap<>();

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private volatile Future<?> future;

	private volatile boolean running;

	public DefaultAutoTradingService(BithumbApiService bithumbApiService, SlackMessagingService slackMessagingService) {
		this.bithumbApiService = bithumbApiService;
		this.slackMessagingService = slackMessagingService;
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
			executions[i] = new TradingScenarioExecution(scenario);
			executions[i].setBasePrice(getCurrentBasePrice(scenario.getCurrency()));
			executions[i].logPrices();
		}
		return executions;
	}

	private int getCurrentBasePrice(Currency currency) {
		OrderBook orderKook = getOrderBook(currency);
		return (int) ((orderKook.getHighestBid() + orderKook.getLowestAsk()) / 2);
	}

	private void runExecution(TradingScenarioExecution execution) {
		try {
			TradingScenario scenario = execution.getScenario();
			Currency currency = scenario.getCurrency();
			OrderBook orderBook = getOrderBook(currency);

			int highestBuyPrice = (int) orderBook.getHighestBid();
			int lowestSellPrice = (int) orderBook.getLowestAsk();

			int basePrice = execution.getBasePrice();
			int buyPriceGapInPercentages = calculateGapInPercentages(basePrice, lowestSellPrice);
			int sellPriceGapInPercentages = calculateGapInPercentages(basePrice, highestBuyPrice);

			TradingStrategy strategy = execution.getStrategy();
			if (buyPriceGapInPercentages <= strategy.getBuySignalGapInPercentages()) {
				log.info("basePrice: {}", basePrice);
				log.info("buyPriceGapInPercentages: {}", buyPriceGapInPercentages);
				log.info("Try to buy now: {}", lowestSellPrice);

				this.slackMessagingService.sendMessage("Try to buy now: " + lowestSellPrice);

				TradePlaceRequest request = new TradePlaceRequest(currency, Currency.KRW, scenario.getCurrencyUnit(),
						lowestSellPrice, TradePlaceType.BID);
				this.bithumbApiService.tradePlace(request);

				// FIXME: This should be replaced with the actual buy price.
				int buyPrice = lowestSellPrice;
				execution.buy(buyPrice);
				execution.logPrices();
				execution.logTotalStatistics();
			}
			else if (sellPriceGapInPercentages >= strategy.getSellSignalGapInPercentages()) {
				log.info("basePrice: {}", basePrice);
				log.info("sellPriceGapInPercentages: {}", sellPriceGapInPercentages);
				log.info("Try to sell now: {}", highestBuyPrice);

				this.slackMessagingService.sendMessage("Try to sell now: " + highestBuyPrice);

				TradePlaceRequest request = new TradePlaceRequest(currency, Currency.KRW, scenario.getCurrencyUnit(),
						highestBuyPrice, TradePlaceType.ASK);
				this.bithumbApiService.tradePlace(request);

				// FIXME: This should be replaced with the actual sell price.
				int sellPrice = highestBuyPrice;
				execution.sell(sellPrice);
				execution.logPrices();
				execution.logTotalStatistics();
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

	private int calculateGapInPercentages(int baseValue, int value) {
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

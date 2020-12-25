package com.izeye.application.bithumbautotrader.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link TradingScenario} execution.
 *
 * @author Johnny Lim
 */
@Data
@Slf4j
public class TradingScenarioExecution {

	private final TradingScenario scenario;

	private final TradingStrategy strategy;

	private final double tradingFeeInPercentages;

	private final long startTime = System.currentTimeMillis();

	private double basePrice;

	private double totalBuyUnits = 0;

	private double totalSellUnits = 0;

	private double totalBuyPrice = 0;

	private double totalSellPrice = 0;

	private double totalTradingFee = 0;

	private final List<Double> buyPrices = new ArrayList<>();

	private final List<Double> sellPrices = new ArrayList<>();

	public TradingScenarioExecution(TradingScenario scenario, double tradingFeeInPercentages) {
		this.scenario = scenario;
		this.strategy = new TradingStrategy(scenario.getSignalGapInPercentages());
		this.tradingFeeInPercentages = tradingFeeInPercentages;
	}

	public void logPrices() {
		log.info("Scenario: {}", this.scenario);
		log.info("Base price: {}", this.basePrice);
		log.info("Next buy price: {}", getNextBuyPrice(this.basePrice, this.strategy.getBuySignalGapInPercentages()));
		log.info("Next sell price: {}",
				getNextSellPrice(this.basePrice, this.strategy.getSellSignalGapInPercentages()));
	}

	private double getNextBuyPrice(double basePrice, double buySignalGapInPercentages) {
		return applyPercentages(basePrice, buySignalGapInPercentages, Math::floor);
	}

	private double getNextSellPrice(double basePrice, double sellSignalGapInPercentages) {
		return applyPercentages(basePrice, sellSignalGapInPercentages, Math::ceil);
	}

	private double applyPercentages(double basePrice, double percentages, Function<Double, Double> function) {
		return function.apply(basePrice * (100 + percentages) / 100);
	}

	public void buy(double price) {
		double currencyUnit = this.scenario.getCurrencyUnit();
		this.totalBuyUnits += currencyUnit;

		double totalPrice = price * currencyUnit;
		double tradingFee = getTradingFee(totalPrice);
		log.info("Bought now: {} (Fee: {})", price, tradingFee);

		this.totalBuyPrice += totalPrice;
		this.totalTradingFee += tradingFee;

		this.basePrice = price;

		this.buyPrices.add(price);
	}

	public void sell(double price) {
		double currencyUnit = this.scenario.getCurrencyUnit();
		this.totalSellUnits += currencyUnit;

		double totalPrice = price * currencyUnit;
		double tradingFee = getTradingFee(totalPrice);
		log.info("Sold now: {} (Fee: {})", price, tradingFee);

		this.totalSellPrice += totalPrice;
		this.totalTradingFee += tradingFee;

		this.basePrice = price;

		this.sellPrices.add(price);
	}

	private double getTradingFee(double price) {
		return price * this.tradingFeeInPercentages / 100;
	}

	public void logTotalStatistics() {
		log.info("Scenario: {}", this.scenario);
		long elapsedTimeInMillis = System.currentTimeMillis() - this.startTime;
		log.info("Elapsed time: {} minute(s)", TimeUnit.MILLISECONDS.toMinutes(elapsedTimeInMillis));
		double totalGain = this.totalSellPrice - this.totalBuyPrice - this.totalTradingFee;
		log.info("Total gain: {}", totalGain);
		double estimatedAdditionalGain = (this.totalBuyUnits - this.totalSellUnits) * this.basePrice;
		log.info("Estimated total gain: {}", totalGain + estimatedAdditionalGain);
		log.info("Total buy units: {}", this.totalBuyUnits);
		log.info("Total sell units: {}", this.totalSellUnits);
		log.info("Buy prices: {}", this.buyPrices);
		log.info("Sell prices: {}", this.sellPrices);
	}

}

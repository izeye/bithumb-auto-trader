package com.izeye.application.bithumbautotrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application for Bithumb auto-trader.
 *
 * @author Johnny Lim
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BithumbAutoTraderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BithumbAutoTraderApplication.class, args);
	}

}

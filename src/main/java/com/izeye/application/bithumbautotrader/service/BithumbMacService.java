package com.izeye.application.bithumbautotrader.service;

/**
 * Service for creating MAC (Message Authentication Code) for Bithumb.
 *
 * @author Johnny Lim
 */
public interface BithumbMacService {

	String createMac(String value);

}

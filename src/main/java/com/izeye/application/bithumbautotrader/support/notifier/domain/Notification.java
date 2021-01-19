package com.izeye.application.bithumbautotrader.support.notifier.domain;

import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Notification.
 *
 * @author Johnny Lim
 */
@Data
public class Notification {

	private final String message;

	private final String detail;

	public Notification(Throwable ex) {
		this.message = ex.getMessage();
		this.detail = ExceptionUtils.getStackTrace(ex);
	}

}

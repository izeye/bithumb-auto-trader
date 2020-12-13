package com.izeye.application.bithumbautotrader.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * {@link ConfigurationProperties} for Bithumb.
 *
 * @author Johnny Lim
 */
@ConfigurationProperties("bithumb")
@ConstructorBinding
@Data
public class BithumbProperties {

    private final String connectKey;
    private final String secretKey;

}

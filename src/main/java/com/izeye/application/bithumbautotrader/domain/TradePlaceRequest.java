package com.izeye.application.bithumbautotrader.domain;

import lombok.Data;

/**
 * Request for trade place.
 *
 * @author Johnny Lim
 */
@Data
public class TradePlaceRequest {

    private final Currency orderCurrency;
    private final Currency paymentCurrency;
    private final double units;
    private final int price;
    private final TradePlaceType type;

}

package com.izeye.application.bithumbautotrader.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.izeye.application.bithumbautotrader.domain.CryptocurrencyExchange;
import com.izeye.application.bithumbautotrader.domain.Currency;
import com.izeye.application.bithumbautotrader.domain.OrderBook;
import com.izeye.application.bithumbautotrader.domain.TradePlaceRequest;

/**
 * Default {@link BithumbApiService}.
 *
 * @author Johnny Lim
 */
@Service
public class DefaultBithumbApiService implements BithumbApiService {

	private static final String URI_PREFIX = "https://api.bithumb.com";

	private static final String URI_TEMPLATE_ORDER_BOOK = URI_PREFIX
			+ "/public/orderbook/{cryptocurrency}_{fiatCurrency}?count=1";

	private static final String PATH_TRADE_PLACE = "/trade/place";

	private static final String URI_TRADE_PLACE = URI_PREFIX + PATH_TRADE_PLACE;

	private static final ParameterizedTypeReference<Map<String, Object>> MAP_STRING_OBJECT = new ParameterizedTypeReference<>() {
	};

	private final BithumbProperties properties;

	private final BithumbMacService bithumbMacService;

	private final WebClient webClient;

	public DefaultBithumbApiService(BithumbProperties properties, BithumbMacService bithumbMacService,
			WebClient.Builder webClientBuilder) {
		this.properties = properties;
		this.bithumbMacService = bithumbMacService;

		this.webClient = webClientBuilder.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Mono<OrderBook> getOrderBook(Currency cryptocurrency, Currency fiatCurrency) {
		return this.webClient.get().uri(URI_TEMPLATE_ORDER_BOOK, cryptocurrency.name(), fiatCurrency.name()).retrieve()
				.bodyToMono(MAP_STRING_OBJECT).map((map) -> {
					OrderBook orderBook = new OrderBook();
					orderBook.setExchange(CryptocurrencyExchange.BITHUMB);
					orderBook.setTargetCurrency(cryptocurrency);
					orderBook.setBaseCurrency(fiatCurrency);
					Map<String, Object> data = (Map<String, Object>) map.get("data");
					List<Map<String, String>> bids = (List<Map<String, String>>) data.get("bids");
					List<Map<String, String>> asks = (List<Map<String, String>>) data.get("asks");
					orderBook.setHighestBid(Double.parseDouble(bids.get(0).get("price")));
					orderBook.setLowestAsk(Double.parseDouble(asks.get(0).get("price")));
					return orderBook;
				});
	}

	@Override
	public Flux<OrderBook> getOrderBooks(Set<Currency> cryptocurrencies, Currency fiatCurrency) {
		return Flux.fromIterable(cryptocurrencies).parallel().runOn(Schedulers.boundedElastic())
				.flatMap((cryptocurrency) -> getOrderBook(cryptocurrency, fiatCurrency))
				.ordered(Comparator.comparing((orderBook) -> orderBook.getTargetCurrency().ordinal()));
	}

	@Override
	public String tradePlace(TradePlaceRequest request) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("order_currency", request.getOrderCurrency().name());
		parameters.add("payment_currency", request.getPaymentCurrency().name());
		parameters.add("units", String.valueOf(request.getUnits()));
		parameters.add("price", String.valueOf(request.getPrice()));
		parameters.add("type", request.getType().name().toLowerCase());

		Map<String, Object> responseMap = request(PATH_TRADE_PLACE, parameters);
		return (String) responseMap.get("order_id");
	}

	private Map<String, Object> request(String path, MultiValueMap<String, String> parameters) {
		String query = parameters.entrySet().stream().map(
				(entry) -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue().get(0), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));

		long nonce = createNonce();

		String data = path + ";" + query + ";" + nonce;

		String mac = this.bithumbMacService.createMac(data);

		Map<String, Object> responseMap = this.webClient.post().uri(URI_TRADE_PLACE)
				.header("Api-Key", this.properties.getConnectKey()).header("Api-Sign", mac)
				.header("Api-Nonce", String.valueOf(nonce)).header("api-client-type", "2")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromFormData(parameters))
				.exchangeToMono((response) -> response.bodyToMono(MAP_STRING_OBJECT)).block();

		String status = (String) responseMap.get("status");
		if (!status.equals("0000")) {
			throw new RuntimeException("Failed to request: " + responseMap);
		}
		return responseMap;
	}

	private long createNonce() {
		return System.currentTimeMillis();
	}

}

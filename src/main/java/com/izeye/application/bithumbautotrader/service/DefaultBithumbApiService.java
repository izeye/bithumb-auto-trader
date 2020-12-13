package com.izeye.application.bithumbautotrader.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.izeye.application.bithumbautotrader.domain.TradePlaceRequest;

/**
 * Default {@link BithumbApiService}.
 *
 * @author Johnny Lim
 */
@Service
public class DefaultBithumbApiService implements BithumbApiService {

	private static final String URI_PREFIX = "https://api.bithumb.com";

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

	@Override
	public String tradePlace(TradePlaceRequest request) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("order_currency", request.getOrderCurrency().name());
		parameters.add("payment_currency", request.getPaymentCurrency().name());
		parameters.add("units", String.valueOf(request.getUnits()));
		parameters.add("price", String.valueOf(request.getPrice()));
		parameters.add("type", request.getType().name().toLowerCase());

		String query = parameters.entrySet().stream().map(
				(entry) -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue().get(0), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));

		long nonce = createNonce();
		String data = PATH_TRADE_PLACE + ";" + query + ";" + nonce;
		String mac = this.bithumbMacService.createMac(data);

		Map<String, Object> responseMap = this.webClient.post().uri(URI_TRADE_PLACE)
				.header("Api-Key", this.properties.getConnectKey()).header("Api-Sign", mac)
				.header("Api-Nonce", String.valueOf(nonce)).header("api-client-type", "2")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromFormData(parameters))
				.exchangeToMono((response) -> response.bodyToMono(MAP_STRING_OBJECT)).block();

		String status = (String) responseMap.get("status");
		switch (status) {
		case "0000":
			return (String) responseMap.get("order_id");

		default:
			throw new RuntimeException("Failed to request: " + responseMap);
		}
	}

	private long createNonce() {
		return System.currentTimeMillis();
	}

}

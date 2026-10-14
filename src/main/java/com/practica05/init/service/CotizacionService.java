package com.practica05.init.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.practica05.init.model.AccionNoEncontradaException;
import com.practica05.init.model.Cotizacion;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class CotizacionService {

    private static final String URL_BASE_YAHOO = "https://query1.finance.yahoo.com";
    private static final Duration TIEMPO_ESPERA = Duration.ofSeconds(10);

    private final WebClient webClient;

    public CotizacionService(ObjectProvider<WebClient.Builder> constructorWebClient) {
        this.webClient = constructorWebClient.getIfAvailable(WebClient::builder)
                .baseUrl(URL_BASE_YAHOO)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();
    }

    public Cotizacion obtenerCotizacion(String simbolo) {
        if (simbolo == null || simbolo.isBlank()) {
            throw new IllegalArgumentException("El símbolo de la acción no puede estar vacío");
        }

        String simboloNormalizado = simbolo.trim().toUpperCase(Locale.ROOT);
        RespuestaYahoo respuestaYahoo = webClient.get()
                .uri(uri -> uri.path("/v8/finance/chart/{simbolo}")
                        .queryParam("range", "1d")
                        .queryParam("interval", "1d")
                        .build(simboloNormalizado))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::isSameCodeAs,
                        respuestaCliente -> Mono.error(new AccionNoEncontradaException(simboloNormalizado)))
                .bodyToMono(RespuestaYahoo.class)
                .block(TIEMPO_ESPERA);

        Metadatos metadatos = extraerMetadatos(respuestaYahoo, simboloNormalizado);
        return new Cotizacion(
                Objects.requireNonNullElse(metadatos.simbolo(), simboloNormalizado),
                metadatos.precioMercado(),
                metadatos.cierreAnterior(),
                metadatos.moneda());
    }

    private static Metadatos extraerMetadatos(RespuestaYahoo respuestaYahoo, String simbolo) {
        return Optional.ofNullable(respuestaYahoo)
                .map(RespuestaYahoo::datos)
                .map(DatosYahoo::resultados)
                .filter(resultados -> !resultados.isEmpty())
                .map(resultados -> resultados.getFirst().metadatos())
                .filter(metadatos -> metadatos.precioMercado() != null && metadatos.cierreAnterior() != null && metadatos.cierreAnterior().signum() != 0)
                .orElseThrow(() -> new AccionNoEncontradaException(simbolo));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record RespuestaYahoo(@JsonProperty("chart") DatosYahoo datos) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DatosYahoo(@JsonProperty("result") List<Resultado> resultados) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Resultado(@JsonProperty("meta") Metadatos metadatos) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Metadatos(
            @JsonProperty("symbol") String simbolo,
            @JsonProperty("currency") String moneda,
            @JsonProperty("regularMarketPrice") BigDecimal precioMercado,
            @JsonProperty("chartPreviousClose") BigDecimal cierreAnterior
    ) {}

}

package com.tubz.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "tbank_correlation_id";

    public String getCorrelationId(HttpHeaders headers) {
        if (headers.get(CORRELATION_ID) != null) {
            List<String> ids = headers.get(CORRELATION_ID);
            assert ids != null;
            return ids
                    .stream()
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    private ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange
                .mutate()
                .request(exchange
                        .getRequest()
                        .mutate()
                        .header(name, value)
                        .build())
                .build();
    }
}

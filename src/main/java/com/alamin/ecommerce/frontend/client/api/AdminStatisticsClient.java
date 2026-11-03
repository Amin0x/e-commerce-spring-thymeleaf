package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.StatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Admin client for statistics on the backend {@code /api/admin/statistics}
 * endpoints.
 */
@Component
@RequiredArgsConstructor
public class AdminStatisticsClient {

    private static final ParameterizedTypeReference<List<StatisticsDto>> STATISTICS_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final String STATISTICS_URL = "/api/admin/statistics";

    public List<StatisticsDto> list() {
        return restTemplate.exchange(
                backendBaseUrl + STATISTICS_URL,
                HttpMethod.GET, null, STATISTICS_LIST).getBody();
    }
}

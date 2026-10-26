package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.Country;
import com.alamin.ecommerce.frontend.dto.State;
import com.alamin.ecommerce.frontend.dto.City;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Client for the backend address/lookup API ({@code /api/addresses}).
 */
@Component
@RequiredArgsConstructor
public class AddressClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final ParameterizedTypeReference<List<Country>> COUNTRY_LIST =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<List<State>> STATE_LIST =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<List<City>> CITY_LIST =
            new ParameterizedTypeReference<>() {
            };

    public List<Country> countries() {
        return restTemplate.exchange(backendBaseUrl + "/api/addresses/countries",
                org.springframework.http.HttpMethod.GET, null, COUNTRY_LIST).getBody();
    }

    public List<State> states() {
        return restTemplate.exchange(backendBaseUrl + "/api/addresses/states",
                org.springframework.http.HttpMethod.GET, null, STATE_LIST).getBody();
    }

    public List<State> statesByCountry(Integer countryId) {
        return restTemplate.exchange(backendBaseUrl + "/api/addresses/states/country/{countryId}",
                org.springframework.http.HttpMethod.GET, null, STATE_LIST, countryId).getBody();
    }

    public List<City> cities() {
        return restTemplate.exchange(backendBaseUrl + "/api/addresses/cities",
                org.springframework.http.HttpMethod.GET, null, CITY_LIST).getBody();
    }

    public List<City> citiesByCountry(Integer countryId) {
        return restTemplate.exchange(backendBaseUrl + "/api/addresses/cities/country/{countryId}",
                org.springframework.http.HttpMethod.GET, null, CITY_LIST, countryId).getBody();
    }

    public List<City> citiesByState(Integer stateId) {
        return restTemplate.exchange(backendBaseUrl + "/api/addresses/cities/state/{stateId}",
                org.springframework.http.HttpMethod.GET, null, CITY_LIST, stateId).getBody();
    }

    public Country createCountry(Country country) {
        return restTemplate.postForObject(backendBaseUrl + "/api/addresses/countries", country, Country.class);
    }

    public State createState(State state) {
        return restTemplate.postForObject(backendBaseUrl + "/api/addresses/states", state, State.class);
    }

    public City createCity(City city) {
        return restTemplate.postForObject(backendBaseUrl + "/api/addresses/cities", city, City.class);
    }

    public void deleteCity(Integer id) {
        restTemplate.delete(backendBaseUrl + "/api/addresses/cities/{id}", id);
    }
}

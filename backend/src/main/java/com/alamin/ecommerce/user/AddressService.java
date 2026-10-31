package com.alamin.ecommerce.user;

import java.util.List;

public interface AddressService {
    List<City> getCities();
    List<City> getCities(Integer stateId);
    City getCity(Integer id);
    List<City> getStateCities(Integer stateId);
    List<City> getCountryCities(Integer countryId);
    City createCity(CityRequest cityRequest);
    City updateCity(CityRequest cityRequest);
    void deleteCity(Integer id);
    Country createCountry(Country country);
    Country updateCountry(Country country);
    Country getCountry(String name);
    Country getCountry(Integer id);
    List<Country> getCountries();
    void deleteCountry(Integer countryId);
    State createState(StateDto state);
    State updateState(StateDto state);
    State getState(Integer id);
    List<State> getStates();
    List<State> getCountryStates(Integer countryId);
    void deleteState(Integer stateId);
}

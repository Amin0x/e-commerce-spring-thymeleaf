package com.alamin.ecommerce.user;

import java.util.List;

public interface AddressService {
    List<City> getCities();
    City getCity(Integer id);
    List<City> getStateCities(Integer stateId);
    List<City> getCountryCities(Integer countryId);
    List<City> findByStateAndCountry(Integer stateId, Integer countryId);
    City createCity(CityFormDto cityFormDto);
    City updateCity(CityFormDto cityFormDto);
    void deleteCity(Integer id);
    List<City> getCityByState(Integer stateId);
    Country createOrUpdateCountry(Country country);
    Country getCountry(String name, boolean nameAr);
    Country getCountry(Integer id);
    List<Country> getCountries();
    void deleteCountry(Integer countryId);
    State createOrUpdateState(StateDto state);
    State getState(Integer id);
    List<State> getStates();
    List<State> getCountryStates(Integer countryId);
    void deleteState(Integer stateId);
}

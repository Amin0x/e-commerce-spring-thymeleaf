package com.alamin.ecommerce.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/address")
public class AddressController {
    private CityRepository cityRepository;
    private CountryRepository countryRepository;
    private StateRepository stateRepository;

    @GetMapping("/city/{id}")
    public City getCityById(int id) {
        return cityRepository.findById(id).orElse(null);
    }

    @PostMapping("/city")
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    public City updateCity(City city) {
        City existingCity = cityRepository.findById(city.getId()).orElse(null);
        if (existingCity != null) {
            existingCity.setName(city.getName());
            existingCity.setState(city.getState());
            return cityRepository.save(existingCity);
        } else {
            return null;
        }
    }

    @GetMapping("/city")
    public List<City> getAllCity() {
        return cityRepository.findAll();
    }

    @GetMapping("/city/state/{state}")
    public List<City> getCityByState(State state) {
        return cityRepository.findByState(state);
    }

    @PostMapping("/city/delete/{id}")
    public void deleteCity(int id) {
        cityRepository.deleteById(id);
    }

    @PostMapping("/country")
    public Country createCountry(Country country) {
        return countryRepository.save(country);
    }

    @PostMapping("/country/update")
    public Country updateCountry(Country country) {
        Country existingCountry = countryRepository.findById(country.getId()).orElse(null);
        if (existingCountry != null) {
            existingCountry.setName(country.getName());
            return countryRepository.save(existingCountry);
        } else {
            return null;
        }
    }

    @GetMapping("/country")
    public List<Country> getAllCountry() {
        return countryRepository.findAll();
    }

    @GetMapping("/country/{id}")
    public List<Country> getCountryByName(String countryName) {
        return countryRepository.findByName(countryName);
    }

    @PostMapping("/state")
    public State createState(State state) {
        State existingState = stateRepository.findByName(state.getName()).orElse(null);
        if (existingState != null) {
            existingState.setName(state.getName());
            existingState.setCountry(state.getCountry());
            return stateRepository.save(existingState);
        } 
        
        return stateRepository.save(state);
    }


    @GetMapping("/state")
    public List<State> getAllState() {
        return stateRepository.findAll();
    }

    @GetMapping("/state/country/{country}")
    public List<State> getStateByCountry(Country country) {
        return stateRepository.findByCountry(country);
    }

    @GetMapping("/state/{id}")
    public State getStateByName(String stateName) {
        return stateRepository.findByName(stateName).orElse(null);
    }

}
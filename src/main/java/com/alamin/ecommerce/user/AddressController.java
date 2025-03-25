package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/address")
public class AddressController {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/city/{id}")
    public City getCityById(@PathVariable int id) {
        return cityRepository.findById(id).orElse(null);
    }

    @PostMapping("/city")
    public City createCity(@RequestBody City city) {
        return cityRepository.save(city);
    }

    @PostMapping("/city/{id}")
    public City updateCity(@RequestBody City city) {
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

    @PostMapping("/country/update/{id}")
    public Country updateCountry(@PathVariable int id,@RequestBody Country country) {
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

    @GetMapping("/country/{countryName}")
    public List<Country> getCountryByName(@PathVariable String countryName) {
        return countryRepository.findByName(countryName);
    }

    @GetMapping("/country/{id}")
    public Country getCountryById(@PathVariable int id) {
        return countryRepository.findById(id).orElseThrow();
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
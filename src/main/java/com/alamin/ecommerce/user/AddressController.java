package com.alamin.ecommerce.user;

import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/address")
public class AddressController {
   
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    public AddressController(CityRepository cityRepository, CountryRepository countryRepository, StateRepository stateRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
    }

    @GetMapping("/city/create")
    public String showCityCreateForm(Model model) {
        model.addAttribute("countries", countryRepository.findAll());
        model.addAttribute("states", stateRepository.findAll());
        model.addAttribute("cities", cityRepository.findAll());
        return "admin/address/city_create";
    }

    @GetMapping("/city")
    public List<City> getAllCity() {
        return cityRepository.findAll();
    }

    @GetMapping("/city/{id}")
    public City getCityById(@PathVariable int id) {
        return cityRepository.findById(id).orElse(null);
    }

    @PostMapping("/city/add")
    public City createCity(@RequestBody City city) {
        if(city.getId() != null){
            return updateCity(city);
        }
        return cityRepository.save(city);
    }

    @PostMapping("/city/{id}")
    public City updateCity(@RequestBody City city) {
        if (city.getId()!=null) {
            throw new IllegalArgumentException("City ID must be null to create a new city");
        }
        City existingCity = cityRepository.findById(city.getId()).orElse(null);
        if (existingCity != null) {
            existingCity.setName(city.getName());
            existingCity.setState(city.getState());
            return cityRepository.save(existingCity);
        } else {
            return null;
        }
    }

    
    @GetMapping("/city/state/{stateId}")
    public List<City> getCityByState(@PathVariable Integer stateId) {
        Optional<State> stateOptional = stateRepository.findById(stateId);
        if (stateOptional.isEmpty()) {
            throw new IllegalArgumentException("State not found with id: " + stateId);
        }
        return cityRepository.findByState(stateOptional.get());
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
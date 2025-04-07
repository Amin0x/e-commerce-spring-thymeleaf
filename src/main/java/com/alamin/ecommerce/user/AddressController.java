package com.alamin.ecommerce.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alamin.ecommerce.exception.ResourceNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;

@Controller
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

    @GetMapping("/city/edit")
    public String showCityCreateForm(@RequestParam int cityId, Model model) {
        model.addAttribute("states", stateRepository.findAll());
        model.addAttribute("cityId", cityId);
        return "admin/address/city_create";
    }


    @GetMapping("/city")
    public ResponseEntity<Object> getCity(@RequestParam(required = false) Integer id, 
                            @RequestParam(required = false) String name) {
        if (id != null) {
            return cityRepository.findById(id)
                    .map(city -> ResponseEntity.<Object>ok(city))
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(cityRepository.findAll());
    }


    @PostMapping(value = "/city/add")
    public ResponseEntity<Object> createCity(@RequestBody City city, @RequestParam("state") Integer state ) {
        System.out.println("City: " + city);
        System.out.println("State: " + state);
        Optional<State> stateOptional = Optional.empty();
        
        try {
            stateOptional = stateRepository.findById(state);
            if (stateOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("State not found with id: " + state);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid state ID: " + state);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }

        if(city.getId() != null){
            city.setState(stateOptional.get());
            return updateCity(city.getId(), city);
        }
        
        try {            
            City city2 = new City();
            city2.setName(city.getName());
            city2.setState(stateOptional.get());
            city2.setDeliveryPrice(city.getDeliveryPrice());
            city2.setDeliveryPriority(city.getDeliveryPriority());
            city2.setEstimatedDelivery(city.getEstimatedDelivery());
            city2.setEstimatedDeliveryUnit(city.getEstimatedDeliveryUnit());
            City saveCity = cityRepository.save(city);
            return ResponseEntity.ok(saveCity);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/city/{id}/update")
    public ResponseEntity<Object> updateCity(@PathVariable Integer id, @RequestBody City city) {
        if (city.getId() != null) {
            throw new ResourceNotFoundException("City ID must be null to create a new city");
        }

        try {
            City existingCity = cityRepository.findById(city.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + city.getId()));

            existingCity.setName(city.getName());
            existingCity.setState(city.getState());
            City saveCity = cityRepository.save(existingCity);
            return ResponseEntity.ok(saveCity);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }

    }

    
    @GetMapping("/city/state/{stateId}")
    public ResponseEntity<Object> getCityByState(@PathVariable Integer stateId) {
        
        try {
            Optional<State> stateOptional = stateRepository.findById(stateId);
            if (stateOptional.isEmpty()) {
                return ResponseEntity.status(404).body("State not found with id: " + stateId);
            }
            List<City> byState = cityRepository.findByState(stateOptional.get());
            return ResponseEntity.ok(byState);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
         

    @PostMapping("/city/{id}/delete")
    public ResponseEntity<Object> deleteCity(int id) {
        try {
            City city = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));
            cityRepository.delete(city);
            return ResponseEntity.ok("City deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/country")
    public ResponseEntity<Object> createOrUpdateCountry(@RequestBody Country country) {
        
        if(country.getId() == null){
            Country saveContry = new Country();
            saveContry.setName(country.getName());
            saveContry.setNameAr(country.getNameAr());
            
            return ResponseEntity.ok(countryRepository.save(saveContry));
        } 

        Country existingCountry = countryRepository.findById(country.getId()).orElse(null);

        if (existingCountry != null) {
            existingCountry.setName(country.getName());
            existingCountry.setNameAr(country.getNameAr());
            return ResponseEntity.ok(countryRepository.save(existingCountry));
        } 
            
        return ResponseEntity.status(404).body("Country not found with id: " + country.getId());           
    }

    @GetMapping("/country")
    public List<Country> getCountry(@RequestParam(required = false) String name, 
            @RequestParam(required = false) String nar, @RequestParam(required = false) Integer id) {
        if (name != null) {
            return countryRepository.findByName(name).stream().toList();
        } else if (nar != null) {
            return countryRepository.findByNameAr(nar).stream().toList();
        } else if (id != null) {
            return countryRepository.findById(id).stream().toList();
        }
        return countryRepository.findAll();
    }

    @PostMapping("/state")
    public ResponseEntity<Object> createOrUpdateState(@RequestBody State state, @RequestParam("country") Integer countryId) {
        Optional<Country> countryOptional = countryRepository.findById(countryId);
        if (countryOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Country not found with id: " + countryId);
        }

        State existingState = null;
        if (state.getId() != null) {
            existingState = stateRepository.findById(state.getId()).orElse(null); 
            if (existingState == null) {
                return ResponseEntity.badRequest().body("State not found with id: " + state.getId());
            }
            
            existingState.setName(state.getName());
            existingState.setCountry(countryOptional.get());
            existingState.setNameAr(state.getNameAr());
            
            return ResponseEntity.ok(stateRepository.save(existingState));
        }

        State newState = new State(); 
        newState.setName(state.getName());
        newState.setCountry(countryOptional.get());
        newState.setNameAr(state.getNameAr());

        return ResponseEntity.ok(stateRepository.save(newState));
    }


    @GetMapping("/state")
    public List<State> getAllState() {
        return stateRepository.findAll();
    }

    @GetMapping("/state/country/{countryId}")
    public List<State> getStateByCountry(Integer countryId) {
        Optional<Country> countryOptional = countryRepository.findById(countryId);
        if (countryOptional.isEmpty()) {
            return null; // or handle the case when country is not found
        }
        return stateRepository.findByCountry(countryOptional.get());
    }

    @GetMapping("/state/{id}")
    public State getStateByName(String stateName) {
        return stateRepository.findByName(stateName).orElse(null);
    }

}
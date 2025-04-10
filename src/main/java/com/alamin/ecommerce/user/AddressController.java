package com.alamin.ecommerce.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alamin.ecommerce.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.*;

@Controller
@RequestMapping("/admin/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/city/list")
    public String showCityList(@RequestParam(required = false) Integer country, 
                @RequestParam(required = false) Integer state, Model model) {
        
        if (country != null && state != null) {
            model.addAttribute("cities", addressService.findByStateAndCountry(state, country));
        } else if (country != null) {
            model.addAttribute("cities", addressService.getCountryCities(country));
        } else if (state != null) {

            model.addAttribute("cities", addressService.getCityByState(state));
        } else {
            model.addAttribute("cities", addressService.getCities());
        }

        model.addAttribute("countries", addressService.getCountries());
        model.addAttribute("currentCountryId", country);

        return "admin/address/city_list";
    }

    @GetMapping("/city/add")
    public String showCityCreateForm(Model model) {
        model.addAttribute("countries", addressService.getCountries());
        model.addAttribute("states", addressService.getStates());
        model.addAttribute("cities", addressService.getCities());
        return "admin/address/city_create";
    }

    @GetMapping("/city/{id}/edit")
    public String showCityEditForm(@PathVariable int id, Model model) {
        Optional<City> cityOptional = Optional.ofNullable(addressService.getCity(id));
        if (cityOptional.isPresent()) {
            model.addAttribute("city", cityOptional.get());
        } else {
            throw new ResourceNotFoundException("City not found with id: " + id);
        }
        model.addAttribute("states", addressService.getCountryStates(cityOptional.get().getCountry().getId()));
        model.addAttribute("countries", addressService.getCountries());
        
        return "admin/address/city_create";
    }

    @GetMapping("/state/add")
    public String showStateAddForm(Model model) {
        model.addAttribute("countries", addressService.getCountries());
        model.addAttribute("stateId", null);
        model.addAttribute("stateName", null);
        return "admin/address/state_create";
    }

    @GetMapping("/state/edit")
    public String showStateEditForm(@RequestParam int sid, Model model) {
        model.addAttribute("countries", addressService.getCountries());
        return "admin/address/state_create";
    }

    @GetMapping("/country/list")
    public String getCountryListPage(Model model){
        model.addAttribute("countries", addressService.getCountries());
        return "admin/address/country_list";
    }

    @GetMapping("/country/edit")
    public String showCountryEditForm(@RequestParam Integer id, Model model){
        Country country = addressService.getCountry(id);
        if (country == null)
            throw new ResourceNotFoundException("not found");

        model.addAttribute("country", country);
        model.addAttribute("formTitle", "Edit Country");
        return "admin/address/country_create";
    }


    @GetMapping("/city")
    public ResponseEntity<Object> getCity(@RequestParam(required = false) Integer id, 
                            @RequestParam(required = false) String name) {
        if (id != null) {
            try {
                City city1 = addressService.getCity(id);
                return ResponseEntity.<Object>ok(city1);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(addressService.getCities());
    }


    @PostMapping("/city")
    public ResponseEntity<Object> createCity(@RequestBody CityFormDto cityFormDto) {
        System.out.println("City: " + cityFormDto);
        try {
            return ResponseEntity.ok(addressService.createCity(cityFormDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/city/{id}/update")
    public ResponseEntity<Object> updateCity(@RequestBody CityFormDto cityFormDto) {
        try {
            return ResponseEntity.ok(addressService.updateCity(cityFormDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @GetMapping("/city/state/{stateId}")
    public ResponseEntity<Object> getCityByState(@PathVariable Integer stateId) {
        
        try {
            List<City> byState = addressService.getCityByState(stateId);
            return ResponseEntity.ok(byState);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
         

    @PostMapping("/city/{id}/delete")
    public ResponseEntity<Object> deleteCity(int id) {
        try {
            addressService.deleteCity(id);
            return ResponseEntity.ok("City deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/country")
    public ResponseEntity<Object> createOrUpdateCountry(@Valid @RequestBody Country country, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(null);
        }
        try {
            return ResponseEntity.ok(addressService.createOrUpdateCountry(country));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/country")
    public ResponseEntity<List<Country>> getCountry(@RequestParam(required = false) String name, 
            @RequestParam(required = false) String nar, @RequestParam(required = false) Integer id) {
        if (name != null) {
            return ResponseEntity.ok(Collections.singletonList(addressService.getCountry(name, false)));
        } else if (nar != null) {
            return ResponseEntity.ok(Collections.singletonList(addressService.getCountry(name, true)));
        } else if (id != null) {
            return ResponseEntity.ok(Collections.singletonList(addressService.getCountry(id)));
        }
        return ResponseEntity.ok(addressService.getCountries());
    }

    @PostMapping("/state")
    public ResponseEntity<Object> createOrUpdateState(@RequestBody State state, @RequestAttribute Integer countryId) {

        try {
            return ResponseEntity.ok(addressService.createOrUpdateState(state, countryId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/state")
    public ResponseEntity<List<State>> getState(@RequestParam(required = false) Integer id, 
            @RequestParam(name = "cid", required = false) Integer countryId) {

        if(id != null){
            try {
                List<State> list = List.of(addressService.getState(id));
                return ResponseEntity.ok(list);
            } catch (Exception e) {
                return ResponseEntity.status(404).body(null);
            }
        } else if (countryId != null) {
            try {
                List<State> list = addressService.getCountryStates(countryId);
                if(list.isEmpty()){
                    return ResponseEntity.status(404).body(null);
                }
                return ResponseEntity.ok(list);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return ResponseEntity.ok(addressService.getStates());
    }

    @GetMapping("/state/country/{countryId}")
    public ResponseEntity<Object> getStatesList(@PathVariable Integer countryId) {
        List<State> states = null;
        try {
            states = addressService.getCountryStates(countryId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        return ResponseEntity.ok(states);
    }

}
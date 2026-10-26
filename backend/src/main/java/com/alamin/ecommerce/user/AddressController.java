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
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/admin/address/city/list")
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

    @GetMapping("/admin/address/city/add")
    public String showCityCreateForm(Model model) {
        model.addAttribute("countries", addressService.getCountries());
        model.addAttribute("states", addressService.getStates());
        model.addAttribute("cities", addressService.getCities());
        return "admin/address/city_create";
    }

    @GetMapping("/admin/address/city/{id}/edit")
    public String showCityEditForm(@PathVariable int id, Model model) {
        City cityOptional = addressService.getCity(id);
        if (cityOptional != null) {
            model.addAttribute("city", cityOptional);
        } else {
            throw new ResourceNotFoundException("City not found with id: " + id);
        }

        if (cityOptional.getCountry() != null) {
            model.addAttribute("states", addressService.getCountryStates(cityOptional.getCountry().getId()));
        }
            
        model.addAttribute("countries", addressService.getCountries());
        
        return "admin/address/city_create";
    }

    @GetMapping("/admin/address/state/add")
    public String showStateAddForm(Model model) {
        model.addAttribute("countries", addressService.getCountries());
        model.addAttribute("stateId", null);
        model.addAttribute("stateName", null);
        return "admin/address/state_create";
    }

    @GetMapping("/admin/address/state/edit")
    public String showStateEditForm(@RequestParam int sid, Model model) {
        model.addAttribute("countries", addressService.getCountries());
        return "admin/address/state_create";
    }

    @GetMapping("/admin/address/country/list")
    public String getCountryListPage(Model model){
        model.addAttribute("countries", addressService.getCountries());
        return "admin/address/country_list";
    }

    @GetMapping("/admin/address/country/edit")
    public String showCountryEditForm(@RequestParam Integer id, Model model){
        Country country = addressService.getCountry(id);
        if (country == null)
            throw new ResourceNotFoundException("not found");

        model.addAttribute("country", country);
        model.addAttribute("formTitle", "Edit Country");
        return "admin/address/country_create";
    }

    @GetMapping("/admin/address/city")
    public ResponseEntity<Object> getCity(@RequestParam(required = false) Integer id, 
                            @RequestParam(required = false) String name) {
        if (id != null) {
            try {
                City city1 = addressService.getCity(id);
                return ResponseEntity.ok(city1);
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            } 
        }
        return ResponseEntity.ok(addressService.getCities());
    }

    @PostMapping("/admin/address/city")
    public ResponseEntity<Object> createCity(@RequestBody CityFormDto cityFormDto) {
        System.out.println("City: " + cityFormDto);
        try {
            return ResponseEntity.ok(addressService.createCity(cityFormDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/admin/address/city/update")
    public ResponseEntity<Object> updateCity(@RequestBody CityFormDto cityFormDto) {
        try {
            return ResponseEntity.ok(addressService.updateCity(cityFormDto));
        } catch (ResourceNotFoundException e) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    
    @GetMapping("/admin/address/city/state/{stateId}")
    public ResponseEntity<Object> getCityByState(@PathVariable Integer stateId) {
        
        try {
            List<City> byState = addressService.getCityByState(stateId);
            return ResponseEntity.ok(byState);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/admin/address/city/{id}/delete")
    public ResponseEntity<Object> deleteCity(int id) {
        try {
            addressService.deleteCity(id);
            return ResponseEntity.ok("City deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/admin/address/country")
    public ResponseEntity<Object> createOrUpdateCountry(@Valid @RequestBody Country country, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(null);
        }
        try {
            return ResponseEntity.ok(addressService.createOrUpdateCountry(country));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin/address/country")
    public ResponseEntity<Object> getCountry(@RequestParam(required = false) String name,
            @RequestParam(required = false) String nar, @RequestParam(required = false) Integer id) {
        if (name != null) {
            try {
                Country country = addressService.getCountry(name, false);
                return ResponseEntity.ok(country);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("something went wrong");
            }
        } else if (nar != null) {
            try {
                Country country = addressService.getCountry(name, true);
                return ResponseEntity.ok(country);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else if (id != null) {
            try {
                Country country = addressService.getCountry(id);
                return ResponseEntity.ok(country);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        return ResponseEntity.ok(addressService.getCountries());
    }

    @PostMapping("/admin/address/state")
    public ResponseEntity<Map<String, Object>> createOrUpdateState(@RequestBody StateDto state) {
        Map<String, Object> response = new HashMap<>();
        try {
            State stateSave = addressService.createOrUpdateState(state);
            response.put("status", HttpStatus.OK);
            response.put("state", stateSave);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            response.put("status", HttpStatus.NOT_FOUND);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            response.put("status", HttpStatus.BAD_REQUEST);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/admin/address/state/{id}")
    public ResponseEntity<Object> getState(@PathVariable Integer id) {

        try {
            State list = addressService.getState(id);
            return ResponseEntity.ok(list);
        } catch(ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        } 
    }
    
    @GetMapping("/admin/address/state/country/{id}")
    public ResponseEntity<Object> getStateByCounrtyId(@PathVariable Integer id) {

        try {
            List<State> list = addressService.getCountryStates(id);
            return ResponseEntity.ok(list);
        } catch(ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/admin/address/state")
    public ResponseEntity<Object> getAllStates(){        
        try {
            return ResponseEntity.ok(addressService.getStates());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

}
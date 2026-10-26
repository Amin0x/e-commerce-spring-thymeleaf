package com.alamin.ecommerce.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Address REST endpoint (countries, states, cities). Delegates to
 * {@link AddressService} and returns JSON for checkout/shipping forms.
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> countries() {
        return ResponseEntity.ok(addressService.getCountries());
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<Country> country(@PathVariable Integer id) {
        return ResponseEntity.ok(addressService.getCountry(id));
    }

    @GetMapping("/states")
    public ResponseEntity<List<State>> states() {
        return ResponseEntity.ok(addressService.getStates());
    }

    @GetMapping("/states/country/{countryId}")
    public ResponseEntity<List<State>> statesByCountry(@PathVariable Integer countryId) {
        return ResponseEntity.ok(addressService.getCountryStates(countryId));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<City>> cities() {
        return ResponseEntity.ok(addressService.getCities());
    }

    @GetMapping("/cities/country/{countryId}")
    public ResponseEntity<List<City>> citiesByCountry(@PathVariable Integer countryId) {
        return ResponseEntity.ok(addressService.getCountryCities(countryId));
    }

    @GetMapping("/cities/state/{stateId}")
    public ResponseEntity<List<City>> citiesByState(@PathVariable Integer stateId) {
        return ResponseEntity.ok(addressService.getCityByState(stateId));
    }

    @PostMapping("/countries")
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        return ResponseEntity.ok(addressService.createOrUpdateCountry(country));
    }

    @PostMapping("/states")
    public ResponseEntity<State> createState(@RequestBody StateDto stateDto) {
        return ResponseEntity.ok(addressService.createOrUpdateState(stateDto));
    }

    @PostMapping("/cities")
    public ResponseEntity<City> createCity(@RequestBody CityFormDto cityFormDto) {
        return ResponseEntity.ok(addressService.createCity(cityFormDto));
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer id) {
        addressService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/states/{id}")
    public ResponseEntity<Void> deleteState(@PathVariable Integer id) {
        addressService.deleteState(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        addressService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}

package com.alamin.ecommerce.frontend.user;

import com.alamin.ecommerce.frontend.client.api.AddressClient;
import com.alamin.ecommerce.frontend.dto.City;
import com.alamin.ecommerce.frontend.dto.Country;
import com.alamin.ecommerce.frontend.dto.State;
import com.alamin.ecommerce.frontend.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Address / lookup pages. All data now comes from the backend REST API through
 * {@link AddressClient} instead of backend services directly.
 */
@Controller
public class AddressController {

    private final AddressClient addressClient;

    public AddressController(AddressClient addressClient) {
        this.addressClient = addressClient;
    }

    @GetMapping("/admin/address/city/list")
    public String showCityList(@RequestParam(required = false) Integer country,
                               @RequestParam(required = false) Integer state, Model model) {

        List<City> cities;
        if (state != null) {
            cities = addressClient.citiesByState(state);
        } else if (country != null) {
            cities = addressClient.citiesByCountry(country);
        } else {
            cities = addressClient.cities();
        }

        model.addAttribute("cities", cities == null ? List.of() : cities);
        model.addAttribute("countries", addressClient.countries() == null ? List.of() : addressClient.countries());
        model.addAttribute("currentCountryId", country);

        return "admin/address/city_list";
    }

    @GetMapping("/admin/address/city/add")
    public String showCityCreateForm(Model model) {
        model.addAttribute("countries", addressClient.countries() == null ? List.of() : addressClient.countries());
        model.addAttribute("states", addressClient.states() == null ? List.of() : addressClient.states());
        model.addAttribute("cities", addressClient.cities() == null ? List.of() : addressClient.cities());
        model.addAttribute("user", new UserDto());
        return "admin/address/city_create";
    }

    @GetMapping("/admin/address/city/{id}/edit")
    public String showCityEditForm(@PathVariable Integer id, Model model) {
        List<City> cities = addressClient.cities() == null ? List.of() : addressClient.cities();
        City city = cities.stream().filter(c -> c.getId() != null && c.getId().equals(id)).findFirst().orElse(null);
        if (city == null) {
            return "error/404";
        }

        List<Country> countries = addressClient.countries() == null ? List.of() : addressClient.countries();
        model.addAttribute("city", city);
        model.addAttribute("countries", countries);

        // The City DTO carries no country relation; no state list for edit.
        model.addAttribute("countryId", null);
        model.addAttribute("states", List.of());
        model.addAttribute("user", new UserDto());
        return "admin/address/city_create";
    }

    @GetMapping("/admin/address/state/add")
    public String showStateAddForm(Model model) {
        model.addAttribute("countries", addressClient.countries() == null ? List.of() : addressClient.countries());
        model.addAttribute("stateId", null);
        model.addAttribute("stateName", null);
        model.addAttribute("user", new UserDto());
        return "admin/address/state_create";
    }

    @GetMapping("/admin/address/state/edit")
    public String showStateEditForm(@RequestParam Integer sid, Model model) {
        List<State> states = addressClient.states() == null ? List.of() : addressClient.states();
        State found = states.stream().filter(s -> s.getId() != null && s.getId().equals(sid)).findFirst().orElse(null);
        model.addAttribute("countries", addressClient.countries() == null ? List.of() : addressClient.countries());
        model.addAttribute("state", found);
        model.addAttribute("stateId", found == null ? null : found.getId());
        model.addAttribute("stateName", found == null ? null : found.getName());
        model.addAttribute("user", new UserDto());
        return "admin/address/state_create";
    }

    @GetMapping("/admin/address/country/list")
    public String getCountryListPage(Model model) {
        model.addAttribute("countries", addressClient.countries() == null ? List.of() : addressClient.countries());
        model.addAttribute("user", new UserDto());
        return "admin/address/country_list";
    }

    @GetMapping("/admin/address/country/edit")
    public String showCountryEditForm(@RequestParam Integer id, Model model) {
        List<Country> countries = addressClient.countries() == null ? List.of() : addressClient.countries();
        Country country = countries.stream().filter(c -> c.getId() != null && c.getId().equals(id)).findFirst().orElse(null);
        if (country == null) {
            return "error/404";
        }

        model.addAttribute("country", country);
        model.addAttribute("formTitle", "Edit Country");
        model.addAttribute("user", new UserDto());
        return "admin/address/country_create";
    }

    @GetMapping("/admin/address/city")
    public ResponseEntity<Object> getCity(@RequestParam(required = false) Integer id,
                                          @RequestParam(required = false) String name) {
        List<City> cities = addressClient.cities() == null ? List.of() : addressClient.cities();
        if (id != null) {
            for (City c : cities) {
                if (c.getId() != null && c.getId().equals(id)) {
                    return ResponseEntity.ok(c);
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City not found");
        }
        return ResponseEntity.ok(cities);
    }

    @PostMapping("/admin/address/city")
    public ResponseEntity<Object> createCity(@RequestBody City city) {
        try {
            return ResponseEntity.ok(addressClient.createCity(city));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/admin/address/city/update")
    public ResponseEntity<Object> updateCity(@RequestBody City city) {
        try {
            return ResponseEntity.ok(addressClient.createCity(city));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/admin/address/city/state/{stateId}")
    public ResponseEntity<Object> getCityByState(@PathVariable Integer stateId) {
        try {
            List<City> byState = addressClient.citiesByState(stateId);
            return ResponseEntity.ok(byState);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/admin/address/city/{id}/delete")
    public ResponseEntity<Object> deleteCity(@PathVariable Integer id) {
        try {
            addressClient.deleteCity(id);
            return ResponseEntity.ok("City deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/admin/address/country")
    public ResponseEntity<Object> createOrUpdateCountry(@RequestBody Country country) {
        try {
            return ResponseEntity.ok(addressClient.createCountry(country));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin/address/country")
    public ResponseEntity<Object> getCountry(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String nar,
                                             @RequestParam(required = false) Integer id) {
        List<Country> countries = addressClient.countries() == null ? List.of() : addressClient.countries();
        if (name != null) {
            for (Country c : countries) {
                if (name.equals(c.getName())) {
                    return ResponseEntity.ok(c);
                }
            }
            return ResponseEntity.badRequest().body("something went wrong");
        } else if (nar != null) {
            for (Country c : countries) {
                if (nar.equals(c.getNameAr())) {
                    return ResponseEntity.ok(c);
                }
            }
            return ResponseEntity.badRequest().body(null);
        } else if (id != null) {
            for (Country c : countries) {
                if (c.getId() != null && c.getId().equals(id)) {
                    return ResponseEntity.ok(c);
                }
            }
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(countries);
    }

    @PostMapping("/admin/address/state")
    public ResponseEntity<Object> createOrUpdateState(@RequestBody State state) {
        try {
            return ResponseEntity.ok(addressClient.createState(state));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/admin/address/state/{id}")
    public ResponseEntity<Object> getState(@PathVariable Integer id) {
        List<State> states = addressClient.states() == null ? List.of() : addressClient.states();
        for (State s : states) {
            if (s.getId() != null && s.getId().equals(id)) {
                return ResponseEntity.ok(s);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("State not found");
    }

    @GetMapping("/admin/address/state/country/{id}")
    public ResponseEntity<Object> getStateByCountryId(@PathVariable Integer id) {
        try {
            List<State> list = addressClient.statesByCountry(id);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/admin/address/state")
    public ResponseEntity<Object> getAllStates() {
        try {
            return ResponseEntity.ok(addressClient.states());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
}

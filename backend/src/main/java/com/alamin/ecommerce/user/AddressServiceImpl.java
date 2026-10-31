package com.alamin.ecommerce.user;

import com.alamin.ecommerce.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    public AddressServiceImpl(CountryRepository countryRepository, CityRepository cityRepository, StateRepository stateRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    public List<City> getCities() {
        return cityRepository.findAll();
    }

    @Override
    public List<City> getCities(Integer stateId) {
        if (stateId == null) {
            return new ArrayList<>();
        }
        Optional<State> stateOptional = stateRepository.findById(stateId);
        if (stateOptional.isEmpty()) {
            return List.of();
        }
        return cityRepository.findByState(stateOptional.get());
    }

    @Override
    public City getCity(Integer id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(""));
    }

    @Override
    public List<City> getStateCities(Integer stateId) {
        Optional<State> stateOptional = stateRepository.findById(stateId);
        if (stateOptional.isEmpty())
            return new ArrayList<>();

        return cityRepository.findByState(stateOptional.get());
    }

    @Override
    public List<City> getCountryCities(Integer countryId) {
        Optional<Country> countryOptional = countryRepository.findById(countryId);
        if (countryOptional.isEmpty())
            return new ArrayList<>();

        return cityRepository.findByCountry(countryOptional.get());
    }

    @Override
    public City createCity(CityRequest cityRequest) {
        State state = null;
        Country country = null;

        if (cityRequest.stateId() != null) {
            state = getState(cityRequest.stateId());
        }

        country = getCountry(cityRequest.countryId());

        City city = new City();
        if (state != null) {
            city.setState(state);
        }
        city.setName(cityRequest.name());
        city.setNameAr(cityRequest.nameAr());
        city.setDeliveryPrice(cityRequest.deliveryPrice());
        city.setDeliveryPriority(cityRequest.deliveryPriority());
        city.setEstimatedDelivery(cityRequest.estimatedDelivery());
        city.setEstimatedDeliveryUnit(cityRequest.estimatedDeliveryUnit());
        city.setCountry(country);
        return cityRepository.save(city);
    }

    @Override
    public City updateCity(CityRequest cityRequest) {
        State state = null;

        if (cityRequest.stateId() != null) {
            try {
                state = getState(cityRequest.stateId());
            } catch (Exception e) {
                System.out.println("getState() Exception: state id=" + cityRequest.stateId());
            }
        }

        try {
            City existingCity = cityRepository.findById(cityRequest.id())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityRequest.id()));
            Country country = getCountry(cityRequest.countryId());
            existingCity.setName(cityRequest.name());
            existingCity.setNameAr(cityRequest.nameAr());
            existingCity.setDeliveryPrice(cityRequest.deliveryPrice());
            existingCity.setDeliveryPriority(cityRequest.deliveryPriority());
            existingCity.setEstimatedDelivery(cityRequest.estimatedDelivery());
            existingCity.setEstimatedDeliveryUnit(cityRequest.estimatedDeliveryUnit());
            existingCity.setCountry(country);
            if (state != null) {
                existingCity.setState(state);
            } else {
                existingCity.setState(null);
            }

            System.out.println(existingCity);
            return cityRepository.save(existingCity);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Something went wrong");
        }
    }

    @Override
    public void deleteCity(Integer id) {
        cityRepository.deleteById(id);
    }

    @Override
    public Country createCountry(Country country) {
        if (country.getId() != null) {
            throw new IllegalArgumentException("Country id must be null for creation");
        }
        Country newCountry = new Country();
        newCountry.setName(country.getName());
        newCountry.setNameAr(country.getNameAr());
        return countryRepository.save(newCountry);
    }

    @Override
    public Country updateCountry(Country country) {
        if (country.getId() == null) {
            throw new IllegalArgumentException("Country id is required for update");
        }
        Country existingCountry = countryRepository.findById(country.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + country.getId()));
        existingCountry.setName(country.getName());
        existingCountry.setNameAr(country.getNameAr());
        return countryRepository.save(existingCountry);
    }

    @Override
    public Country getCountry(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Country name cannot be null or empty");
        }

        return countryRepository.findByNameOrNameAr(name, name)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with name: " + name));
    }

    @Override
    public Country getCountry(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Country id cannot be null");
        }
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + id));
    }

    @Override
    public List<Country> getCountries() {
        return countryRepository.findAll();
    }

    @Override
    public void deleteCountry(Integer countryId) {
        countryRepository.deleteById(countryId);
    }

    @Override
    public State createState(StateDto state) {
        if (state.id() != null) {
            throw new IllegalArgumentException("State id must be null for creation");
        }
        Country country = countryRepository.findById(state.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + state.countryId()));

        State newState = new State();
        newState.setName(state.name());
        newState.setCountry(country);
        newState.setNameAr(state.nameAr());

        return stateRepository.save(newState);
    }

    @Override
    public State updateState(StateDto state) {
        if (state.id() == null) {
            throw new IllegalArgumentException("State id is required for update");
        }
        Country country = countryRepository.findById(state.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + state.countryId()));

        State existingState = stateRepository.findById(state.id())
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + state.id()));

        existingState.setName(state.name());
        existingState.setCountry(country);
        existingState.setNameAr(state.nameAr());

        return stateRepository.save(existingState);
    }

    @Override
    public State getState(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("State id cannot be null");
        }
        return stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + id));
    }

    @Override
    public List<State> getStates() {
        return stateRepository.findAll();
    }

    @Override
    public List<State> getCountryStates(Integer countryId) {
        Optional<Country> countryOptional = countryRepository.findById(countryId);
        if (countryOptional.isEmpty())
            throw new ResourceNotFoundException("");

        return stateRepository.findByCountry(countryOptional.get());
    }

    @Override
    public void deleteState(Integer stateId) {
        stateRepository.deleteById(stateId);
    }
}

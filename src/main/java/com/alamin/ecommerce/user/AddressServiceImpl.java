package com.alamin.ecommerce.user;

import com.alamin.ecommerce.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public City getCity(Integer id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(""));
    }

    @Override
    public List<City> getStateCities(Integer stateId) {
        Optional<State> stateOptional = stateRepository.findById(stateId);
        if (stateOptional.isEmpty())
            throw new ResourceNotFoundException("");

        return cityRepository.findByState(stateOptional.get());
    }

    @Override
    public List<City> getCountryCities(Integer countryId) {
        Optional<Country> countryOptional = countryRepository.findById(countryId);
        if (countryOptional.isEmpty())
            throw new ResourceNotFoundException("");

        return cityRepository.findByCountry(countryOptional.get());
    }

    @Override
    public List<City> findByStateAndCountry(Integer stateId, Integer countryId) {
        return List.of();
    }

    @Override
    public City createCity(CityFormDto cityFormDto) {
        Optional<State> stateOptional;

        try {
            stateOptional = stateRepository.findById(cityFormDto.getStateId());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid state ID: " + cityFormDto.getStateId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("");
        }

        if (stateOptional.isEmpty()) {
            throw new ResourceNotFoundException("State not found with id: " + cityFormDto.getStateId());
        }

        City city2 = new City();
        city2.setName(cityFormDto.getName());
        city2.setState(stateOptional.get());
        city2.setDeliveryPrice(cityFormDto.getDeliveryPrice());
        city2.setDeliveryPriority(cityFormDto.getDeliveryPriority());
        city2.setEstimatedDelivery(cityFormDto.getEstimatedDelivery());
        city2.setEstimatedDeliveryUnit(cityFormDto.getEstimatedDeliveryUnit());
        return cityRepository.save(city2);

    }

    @Override
    public City updateCity(CityFormDto cityFormDto) {
        Optional<State> stateOptional = stateRepository.findById(cityFormDto.getStateId());
        Optional<Country> countryOptional = countryRepository.findById(cityFormDto.getCountryId());
        if (stateOptional.isEmpty() || countryOptional.isEmpty()){
            throw new ResourceNotFoundException("");
        }

        try {
            City existingCity = cityRepository.findById(cityFormDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityFormDto.getId()));

            existingCity.setName(cityFormDto.getName());
            existingCity.setState(stateOptional.get());
            return cityRepository.save(existingCity);
        } catch (Exception e) {
            throw new ResourceNotFoundException("");
        }
    }

    @Override
    public void deleteCity(Integer id) {
        cityRepository.deleteById(id);
    }

    @Override
    public List<City> getCityByState(Integer stateId) {
        Optional<State> stateOptional = stateRepository.findById(stateId);
        if (stateOptional.isEmpty())
            throw new ResourceNotFoundException("");

        return cityRepository.findByState(stateOptional.get());
    }

    @Override
    public Country createOrUpdateCountry(Country country) {


        if(country.getId() == null){
            // Update the existing country
            Country saveContry = new Country();
            saveContry.setName(country.getName());
            saveContry.setNameAr(country.getNameAr());

            return countryRepository.save(saveContry);
        }

        Country existingCountry = countryRepository.findById(country.getId()).orElse(null);

        if (existingCountry != null) {
            // Update the existing country
            existingCountry.setName(country.getName());
            existingCountry.setNameAr(country.getNameAr());

            return countryRepository.save(existingCountry);
        }

        throw new ResourceNotFoundException("Country not found with id: " + country.getId());
    }

    @Override
    public Country getCountry(String name, boolean nameAr) {
        Optional<Country> countryOptional;

        if (nameAr){
            countryOptional = countryRepository.findByNameAr(name);
        } else {
            countryOptional = countryRepository.findByName(name);
        }

        if (countryOptional.isEmpty())
            throw new ResourceNotFoundException("");

        return countryOptional.get();
    }

    @Override
    public Country getCountry(Integer id) {
        Optional<Country> countryOptional = countryRepository.findById(id);
        if (countryOptional.isEmpty())
            throw new ResourceNotFoundException("");

        return countryOptional.get();
    }

    @Override
    public List<Country> getCountries() {
        return List.of();
    }

    @Override
    public void deleteCountry(Integer countryId) {
        countryRepository.deleteById(countryId);
    }

    @Override
    public State createOrUpdateState(State state, Integer countryId) {
        Optional<Country> countryOptional;
        try {
            countryOptional = countryRepository.findById(countryId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Country is empty");
        }

        if (countryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Country not found with id: " + countryId);
        }

        State existingState = null;
        if (state.getId() != null) {
            try {
                existingState = stateRepository.findById(state.getId()).orElse(null);
            } catch (Exception e) {
                throw new ResourceNotFoundException("State null id:");
            }
            if (existingState == null) {
                throw new ResourceNotFoundException("State not found with id: " + state.getId());
            }

            existingState.setName(state.getName());
            existingState.setCountry(countryOptional.get());
            existingState.setNameAr(state.getNameAr());

            return stateRepository.save(existingState);
        }

        State newState = new State();
        newState.setName(state.getName());
        newState.setCountry(countryOptional.get());
        newState.setNameAr(state.getNameAr());

        return stateRepository.save(newState);
    }

    @Override
    public State getState(Integer id) {
        Optional<State> stateOptional = stateRepository.findById(id);
        if (stateOptional.isEmpty())
            throw new ResourceNotFoundException("");

        return stateOptional.get();
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

    }
}

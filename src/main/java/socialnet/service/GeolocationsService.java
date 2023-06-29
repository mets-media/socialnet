package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.GeolocationRs;
import socialnet.repository.CityRepository;
import socialnet.repository.CountryRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GeolocationsService {
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    public List<GeolocationRs> findAllCountry() {
        return countryRepository.findAll().stream()
            .map(c -> new GeolocationRs(c.getName())).collect(Collectors.toList());
    }

    public List<GeolocationRs> getCitiesByCountryAndStarts(String country, String starts) {
        return cityRepository.getCitiesByStarts(country, starts).stream()
            .map(c -> new GeolocationRs(c.getName())).collect(Collectors.toList());
    }

    public List<GeolocationRs> getCitiesByCountry(String country) {
        return cityRepository.getCitiesByCountry(country).stream()
            .map(c -> new GeolocationRs(c.getName())).collect(Collectors.toList());
    }

    public List<GeolocationRs> getCitiesUses(String country) {
        return cityRepository.getCitiesFromPersons(country).stream()
                .map(c -> new GeolocationRs(c.getName())).collect(Collectors.toList());
    }

}

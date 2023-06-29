package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.api.response.RegionStatisticsRs;
import socialnet.model.Country;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CountryRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Country> findAll() {
        return jdbcTemplate.query("Select * from Countries order by name",
                new Object[]{},
                new BeanPropertyRowMapper<>(Country.class));
    }

    public List<RegionStatisticsRs> findCountryUsers() {
        try {
            return jdbcTemplate.query("SELECT DISTINCT countries.id, countries.name, (SELECT COUNT(*) FROM persons" +
                " WHERE countries.international_name=persons.country) FROM countries ", countryStatisticsRsRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    private final RowMapper<RegionStatisticsRs> countryStatisticsRsRowMapper = (resultSet, rowNum) -> {
        RegionStatisticsRs rs = new RegionStatisticsRs();
        rs.setRegion(resultSet.getString(2));
        rs.setCountUsers(resultSet.getInt(3));
        return rs;
    };

    public Country getCountry(String country) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM countries WHERE countries.international_name=?" +
                            " ORDER BY name", new BeanPropertyRowMapper<>(Country.class), country);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}

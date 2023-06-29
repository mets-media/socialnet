package socialnet.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import socialnet.repository.CountryRepository;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class initData {
    @Bean
    public CommandLineRunner commandLineRunner(CityParser cityParser,
                                               CountryRepository countryRepository,
                                               JdbcTemplate jdbcTemplate) {
        return args -> {
            if (args.length > 1) {
                switch (args[0].toLowerCase()) {
                    case "-city": {
                        /** -city RU */
                        if (args.length < 2) break;

                        URL url = new URL("https://bulk.openweathermap.org/sample/city.list.json.gz");
                        InputStream inputStream = url.openStream();

                        //TODO Переделать на Pipe InputStream - без сохранения в файл
                        (new GzipFile()).unGZipToFile(inputStream, url.toString().substring(url.toString().lastIndexOf("/") + 1));
                        //Files.copy(inputStream, new File("city.list.json.gz").toPath());

                        String sqlCities = cityParser.getInsertSqlCityByCountry("city.list.json", args[1].toUpperCase());
                        jdbcTemplate.update(sqlCities);

                        if (args.length == 3) {
                            Path path = Paths.get(String.format("%s_%s.sql", args[1], "cities"));
                            byte[] stringToByte = sqlCities.getBytes();
                            Files.write(path, stringToByte);
                            log.info(sqlCities);
                        }
                        break;
                    }
                }
            }
        };
    }
}

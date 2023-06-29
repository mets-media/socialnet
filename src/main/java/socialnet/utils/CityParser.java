package socialnet.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Component;
import socialnet.exception.ParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CityParser {

    private final Reflection reflection;

    public String getInsertSqlCityByCountry(String fileName, String  countryCode) throws FileNotFoundException {
        Set<CityDto> allCities = getAllCity(fileName);

        var citiesByCountry = allCities.stream()
                .filter(cityDto -> cityDto.code2.equals(countryCode))
                .collect(Collectors.toList());


        String delimiter = "";
        StringBuilder result = new StringBuilder();
        for (CityDto cityDto : citiesByCountry) {
            result.append(delimiter.concat("(").concat(reflection.getStringValues(cityDto)).concat(")"));
            delimiter = ",\n";
        }
        return "Insert into Cities (" + String.join(", ", reflection.getAllFields(citiesByCountry.get(0)))   +
                ") values \n".concat(result.toString().concat("\non conflict on constraint unique_weather_id do nothing"));


/*
        String sqlCommand = "Insert into Cities (" + String.join(", ", reflection.getAllFields(citiesByCountry.get(0)))   + ") values ";
        StringBuilder result = new StringBuilder();
        for (CityDto cityDto : citiesByCountry) {
            result.append(sqlCommand.concat("(").concat(reflection.getStringValues(cityDto)).concat(");\n"));
        }


        return result.toString();
*/
    }

    private Set<CityDto> getAllCity(String fileName) throws FileNotFoundException {

        FileReader fileReader = new FileReader(fileName);
        JSONParser parser = new JSONParser(fileReader);

        try {
            List<LinkedHashMap<String, String>> list = (List<LinkedHashMap<String, String>>) parser.parse();

            Set<CityDto> cityDtoSet = new HashSet<>();

            for (LinkedHashMap<String, String> cityInfo : list) {

                var setElement = cityInfo.entrySet();
                var iterator = setElement.iterator();

                List<Object> values = new ArrayList<>();
                while (iterator.hasNext()) {
                    var item = (Map.Entry) iterator.next();
                    var currentValue = item.getValue();
                    var cls = currentValue.getClass();

                    if (cls != LinkedHashMap.class) {
                        values.add(item.getValue());
                    } else {
                        var coordElement = ((LinkedHashMap<String, String>) currentValue).entrySet();
                        var coordIterator = coordElement.iterator();
                        while (coordIterator.hasNext()) {
                            var coordItem = coordIterator.next();
                            values.add(coordItem.getValue());
                        }
                    }
                }

                try {
                    cityDtoSet.add(new CityDto(values));
                } catch (Exception e) {
                    if (values.get(0).getClass() == BigDecimal.class) {
                        var citiCode = ((BigDecimal)values.get(0)).toBigInteger();
                        values.remove(0);
                        values.add(0,citiCode);
                        cityDtoSet.add(new CityDto(values));
                    }
                }

            }
            return cityDtoSet;
        } catch (ParseException e) {
            throw new ParserException(e.getMessage());
        }
    }

    @Getter
    private static class CityDto {
        private final BigInteger openWeatherId;
        private final String name;
        private final String state;
        private final String code2;
        private final BigDecimal lon;
        private final BigDecimal lat;

        public CityDto(List<Object> values) {
            this.openWeatherId = (BigInteger) values.get(0);
            this.name = (String)values.get(1);
            this.state = (String)values.get(2);
            this.code2 = (String)values.get(3);
            this.lon = (BigDecimal)values.get(4);
            this.lat = (BigDecimal)values.get(5);
        }
    }
}

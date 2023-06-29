package socialnet.mappers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.stereotype.Component;
import socialnet.api.response.WeatherRs;
import socialnet.model.Weather;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:49+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
@Component
public class WeatherMapperImpl implements WeatherMapper {

    private final DatatypeFactory datatypeFactory;

    public WeatherMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public Weather toModel(WeatherRs weatherRs) {
        if ( weatherRs == null ) {
            return null;
        }

        Weather weather = new Weather();

        weather.setCity( weatherRs.getCity() );
        weather.setClouds( weatherRs.getClouds() );
        if ( weatherRs.getTemp() != null ) {
            weather.setTemp( Float.parseFloat( weatherRs.getTemp() ) );
        }

        weather.setDate( currentTime() );

        return weather;
    }

    @Override
    public WeatherRs toResponse(Weather weather) {
        if ( weather == null ) {
            return null;
        }

        WeatherRs weatherRs = new WeatherRs();

        weatherRs.setDate( xmlGregorianCalendarToString( dateToXmlGregorianCalendar( weather.getDate() ), "yyyy-MM-dd'T'HH:mm:ss" ) );
        weatherRs.setCity( weather.getCity() );
        weatherRs.setClouds( weather.getClouds() );
        if ( weather.getTemp() != null ) {
            weatherRs.setTemp( String.valueOf( weather.getTemp() ) );
        }

        return weatherRs;
    }

    private String xmlGregorianCalendarToString( XMLGregorianCalendar xcal, String dateFormat ) {
        if ( xcal == null ) {
            return null;
        }

        if (dateFormat == null ) {
            return xcal.toString();
        }
        else {
            Date d = xcal.toGregorianCalendar().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
            return sdf.format( d );
        }
    }

    private XMLGregorianCalendar dateToXmlGregorianCalendar( Date date ) {
        if ( date == null ) {
            return null;
        }

        GregorianCalendar c = new GregorianCalendar();
        c.setTime( date );
        return datatypeFactory.newXMLGregorianCalendar( c );
    }
}

package socialnet.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import socialnet.repository.PersonRepository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class Converter {

    private final PersonRepository personRepository;
    @Value("${defaultPhoto}")
    private String defaultPhoto;


    public Long dateToMillisec(String dateStr
    ) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = simpleDateFormat.parse(dateStr);
        return date.getTime();
    }

    public Timestamp dateToTimeStamp(String dateStr) {
        dateStr = dateStr.substring(0,19).replace("T"," ");
        return Timestamp.valueOf(dateStr);
    }

    public String checkPhotoId(String photoId) {
        if (photoId == null)
            return defaultPhoto;
        else
            return photoId;

    }

}

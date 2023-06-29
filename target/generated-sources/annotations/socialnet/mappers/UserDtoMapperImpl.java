package socialnet.mappers;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import socialnet.api.request.UserRq;
import socialnet.api.request.UserUpdateDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:50+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
@Component
public class UserDtoMapperImpl implements UserDtoMapper {

    @Override
    public UserUpdateDto toDto(UserRq userRq) {
        if ( userRq == null ) {
            return null;
        }

        UserUpdateDto userUpdateDto = new UserUpdateDto();

        userUpdateDto.setPhoto( userRq.getPhotoId() );
        userUpdateDto.setAbout( userRq.getAbout() );
        userUpdateDto.setCity( userRq.getCity() );
        userUpdateDto.setCountry( userRq.getCountry() );
        userUpdateDto.setFirstName( userRq.getFirstName() );
        userUpdateDto.setLastName( userRq.getLastName() );
        userUpdateDto.setPhone( userRq.getPhone() );

        userUpdateDto.setBirthDate( dateToTimeStamp(userRq.getBirthDate()) );

        return userUpdateDto;
    }
}

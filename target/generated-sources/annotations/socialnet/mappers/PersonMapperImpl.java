package socialnet.mappers;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import socialnet.api.response.PersonRs;
import socialnet.model.Person;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:50+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
@Component
public class PersonMapperImpl implements PersonMapper {

    @Override
    public PersonRs toDTO(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonRs personRs = new PersonRs();

        personRs.setToken( person.getChangePasswordToken() );
        personRs.setUserDeleted( person.getIsDeleted() );
        personRs.setMessagesPermission( person.getMessagePermissions() );
        personRs.setAbout( person.getAbout() );
        personRs.setBirthDate( person.getBirthDate() );
        personRs.setCity( person.getCity() );
        personRs.setCountry( person.getCountry() );
        personRs.setEmail( person.getEmail() );
        personRs.setFirstName( person.getFirstName() );
        personRs.setId( person.getId() );
        personRs.setIsBlocked( person.getIsBlocked() );
        personRs.setLastName( person.getLastName() );
        personRs.setLastOnlineTime( person.getLastOnlineTime() );
        personRs.setPhone( person.getPhone() );
        personRs.setPhoto( person.getPhoto() );
        personRs.setRegDate( person.getRegDate() );

        personRs.setOnline( isOnline(person.getOnlineStatus()) );

        return personRs;
    }
}

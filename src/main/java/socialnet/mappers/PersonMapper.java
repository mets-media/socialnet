package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import socialnet.api.response.PersonRs;
import socialnet.model.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mapping(source = "changePasswordToken", target = "token")
    @Mapping(source = "isDeleted", target = "userDeleted")
    @Mapping(source = "messagePermissions", target = "messagesPermission")
    @Mapping(expression = "java(isOnline(person.getOnlineStatus()))", target = "online")
    PersonRs toDTO(Person person);

    default boolean isOnline(String status) {
        return status != null && status.equals("ONLINE");
    }
}

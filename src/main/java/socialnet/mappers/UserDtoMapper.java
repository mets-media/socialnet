package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import socialnet.api.request.UserRq;
import socialnet.api.request.UserUpdateDto;

import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    @Mapping(expression = "java(dateToTimeStamp(userRq.getBirthDate()))", target = "birthDate")
    @Mapping(source = "photoId", target = "photo")
    UserUpdateDto toDto(UserRq userRq);

    default Timestamp dateToTimeStamp(String dateStr) {
        if (dateStr == null) return null;
        dateStr = dateStr.substring(0,19).replace("T"," ");
        return Timestamp.valueOf(dateStr);
    }
}

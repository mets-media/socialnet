package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import socialnet.api.response.NotificationRs;
import socialnet.model.Notification;

@Mapper
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationRs toDTO(Notification notification);
}

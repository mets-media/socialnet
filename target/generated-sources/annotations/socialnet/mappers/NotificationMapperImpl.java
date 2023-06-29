package socialnet.mappers;

import javax.annotation.processing.Generated;
import socialnet.api.response.NotificationRs;
import socialnet.model.Notification;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:50+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationRs toDTO(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationRs notificationRs = new NotificationRs();

        if ( notification.getId() != null ) {
            notificationRs.setId( notification.getId().intValue() );
        }
        notificationRs.setNotificationType( notification.getNotificationType() );
        notificationRs.setSentTime( notification.getSentTime() );

        return notificationRs;
    }
}

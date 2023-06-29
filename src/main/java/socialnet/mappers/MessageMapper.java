package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import socialnet.api.response.MessageRs;
import socialnet.model.Message;

import java.util.List;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageRs toDTO(Message message);

    List<MessageRs> toDTO(List<Message> messages);
}

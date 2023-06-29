package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import socialnet.api.response.DialogRs;
import socialnet.model.Dialog;

@Mapper
public interface DialogMapper {
    DialogMapper INSTANCE = Mappers.getMapper(DialogMapper.class);

    @Mapping(source = "secondPersonId", target = "recipientId")
    @Mapping(source = "firstPersonId", target = "authorId")
    DialogRs toDTO(Dialog dialog);
}

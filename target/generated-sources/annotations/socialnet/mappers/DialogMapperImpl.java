package socialnet.mappers;

import javax.annotation.processing.Generated;
import socialnet.api.response.DialogRs;
import socialnet.api.response.DialogRs.DialogRsBuilder;
import socialnet.model.Dialog;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:50+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
public class DialogMapperImpl implements DialogMapper {

    @Override
    public DialogRs toDTO(Dialog dialog) {
        if ( dialog == null ) {
            return null;
        }

        DialogRsBuilder dialogRs = DialogRs.builder();

        dialogRs.recipientId( dialog.getSecondPersonId() );
        dialogRs.authorId( dialog.getFirstPersonId() );
        dialogRs.id( dialog.getId() );

        return dialogRs.build();
    }
}

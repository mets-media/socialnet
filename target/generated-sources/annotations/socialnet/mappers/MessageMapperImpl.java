package socialnet.mappers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import socialnet.api.response.MessageRs;
import socialnet.api.response.MessageRs.MessageRsBuilder;
import socialnet.model.Message;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:50+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
public class MessageMapperImpl implements MessageMapper {

    private final DatatypeFactory datatypeFactory;

    public MessageMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public MessageRs toDTO(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageRsBuilder messageRs = MessageRs.builder();

        messageRs.authorId( message.getAuthorId() );
        messageRs.id( message.getId() );
        messageRs.messageText( message.getMessageText() );
        messageRs.readStatus( message.getReadStatus() );
        messageRs.recipientId( message.getRecipientId() );
        messageRs.time( xmlGregorianCalendarToString( dateToXmlGregorianCalendar( message.getTime() ), null ) );

        return messageRs.build();
    }

    @Override
    public List<MessageRs> toDTO(List<Message> messages) {
        if ( messages == null ) {
            return null;
        }

        List<MessageRs> list = new ArrayList<MessageRs>( messages.size() );
        for ( Message message : messages ) {
            list.add( toDTO( message ) );
        }

        return list;
    }

    private String xmlGregorianCalendarToString( XMLGregorianCalendar xcal, String dateFormat ) {
        if ( xcal == null ) {
            return null;
        }

        if (dateFormat == null ) {
            return xcal.toString();
        }
        else {
            Date d = xcal.toGregorianCalendar().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
            return sdf.format( d );
        }
    }

    private XMLGregorianCalendar dateToXmlGregorianCalendar( Date date ) {
        if ( date == null ) {
            return null;
        }

        GregorianCalendar c = new GregorianCalendar();
        c.setTime( date );
        return datatypeFactory.newXMLGregorianCalendar( c );
    }
}

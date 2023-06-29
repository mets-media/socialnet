package socialnet.mappers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.stereotype.Component;
import socialnet.api.request.PostRq;
import socialnet.api.response.PostRs;
import socialnet.model.Post;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:50+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
@Component
public class PostMapperImpl implements PostMapper {

    private final DatatypeFactory datatypeFactory;

    public PostMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public PostRs toDTO(Post post) {
        if ( post == null ) {
            return null;
        }

        PostRs postRs = new PostRs();

        postRs.setId( post.getId() );
        postRs.setIsBlocked( post.getIsBlocked() );
        postRs.setPostText( post.getPostText() );
        postRs.setTime( xmlGregorianCalendarToString( dateToXmlGregorianCalendar( post.getTime() ), null ) );
        postRs.setTitle( post.getTitle() );

        postRs.setType( getType(post) );

        return postRs;
    }

    @Override
    public Post postRqToPost(PostRq postRq) {
        if ( postRq == null ) {
            return null;
        }

        Post post = new Post();

        post.setPostText( postRq.getPostText() );
        post.setTitle( postRq.getTitle() );

        post.setIsDeleted( false );
        post.setIsBlocked( false );

        return post;
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

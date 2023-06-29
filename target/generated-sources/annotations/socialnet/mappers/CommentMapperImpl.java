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
import org.springframework.stereotype.Component;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.model.Comment;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T17:48:50+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Amazon.com Inc.)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    private final DatatypeFactory datatypeFactory;

    public CommentMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public List<CommentRs> toDTO(List<Comment> list) {
        if ( list == null ) {
            return null;
        }

        List<CommentRs> list1 = new ArrayList<CommentRs>( list.size() );
        for ( Comment comment : list ) {
            list1.add( toDTO( comment ) );
        }

        return list1;
    }

    @Override
    public CommentRs toDTO(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentRs commentRs = new CommentRs();

        commentRs.setCommentText( comment.getCommentText() );
        commentRs.setId( comment.getId() );
        commentRs.setIsBlocked( comment.getIsBlocked() );
        commentRs.setIsDeleted( comment.getIsDeleted() );
        commentRs.setParentId( comment.getParentId() );
        commentRs.setPostId( comment.getPostId() );
        commentRs.setTime( xmlGregorianCalendarToString( dateToXmlGregorianCalendar( comment.getTime() ), null ) );

        return commentRs;
    }

    @Override
    public Comment toModel(CommentRq commentRq) {
        if ( commentRq == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setCommentText( commentRq.getCommentText() );
        if ( commentRq.getParentId() != null ) {
            comment.setParentId( commentRq.getParentId().longValue() );
        }

        return comment;
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

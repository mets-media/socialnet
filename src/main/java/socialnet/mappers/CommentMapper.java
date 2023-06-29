package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    List<CommentRs> toDTO(List<Comment> list);

    CommentRs toDTO(Comment comment);

    Comment toModel(CommentRq commentRq);
}

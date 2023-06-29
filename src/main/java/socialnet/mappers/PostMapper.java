package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import socialnet.api.request.PostRq;
import socialnet.api.response.PostRs;
import socialnet.model.Post;
import socialnet.model.enums.PostType;

import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(expression = "java(getType(post))", target = "type")
    PostRs toDTO(Post post);

    @Mapping(ignore = true, target = "timeDelete")
    @Mapping(source = "postText", target = "postText")
    @Mapping(ignore = true, target = "id")
    @Mapping(expression = "java(false)", target = "isDeleted")
    @Mapping(expression = "java(false)", target = "isBlocked")
    Post postRqToPost(PostRq postRq);

    default String getType(Post post) {
        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            return PostType.DELETED.toString();
        }
        Timestamp postTime = post.getTime();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (postTime.after(now)) {
            return PostType.QUEUED.toString();
        }
        return PostType.POSTED.toString();
    }
}

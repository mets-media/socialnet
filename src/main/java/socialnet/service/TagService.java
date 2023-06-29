package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.model.Tag;
import socialnet.repository.TagRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public String getPostByQueryTags(String[] tags) {
        List<Tag> tags1 = tagRepository.getTagsIdByName(Arrays.asList(tags));
        List<Long> tagsId = new ArrayList<>();
        tags1.forEach(tag -> tagsId.add(tag.getId()));
        StringBuilder str = new StringBuilder();
        str.append(" (");
        tagsId.forEach(tagId -> str.append("post2tag.tag_id = ").append(tagId).append(" OR "));
        str.append(") ");
        if (str.length() > 4) {
            return str.substring(0, str.length() - 5) + ") ";
        } else {
            return str.toString();
        }
    }
}

package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Post2Tag;
import socialnet.model.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class Post2TagRepository {
    private final JdbcTemplate jdbcTemplate;

    public void save(Post2Tag post2Tag) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("tags").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("tag_id", post2Tag.getTagId());
        values.put("post_id", post2Tag.getPostId());

        simpleJdbcInsert.execute(values);
    }

    public List<Tag> findTagsByPostId(Long postId) {
        return jdbcTemplate.query(
                "Select t.id, t.ta  g From post2tag p\n" +
                        "join tags t on (p.post_id = t.id)\n" +
                        "Where p.post_id = ?",
                new Object[]{postId},
                new BeanPropertyRowMapper<>(Tag.class));
    }

}

package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Tag;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Tag> findByPostId(Long postId) {
        String select = "SELECT * FROM tags JOIN post2tag ON tags.id = post2tag.tag_id AND post_id = ?";
        try {
            return jdbcTemplate.query(select, tagRowMapper, postId);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public long save(Tag tag, long postId) {
        SimpleJdbcInsert simpleJdbcInsert1 = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert1.withTableName("tags").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("tag", tag.getTag());
        Map<String, Object> valuesPost2Tags = new HashMap<>();
        long tagId = simpleJdbcInsert1.executeAndReturnKey(values).longValue();
        SimpleJdbcInsert simpleJdbcInsert2 = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert2.withTableName("post2tag").usingGeneratedKeyColumns("id");
        valuesPost2Tags.put("tag_id", tagId);
        valuesPost2Tags.put("post_id", postId);
        simpleJdbcInsert2.execute(valuesPost2Tags);
        return tagId;
    }

    private final RowMapper<Tag> tagRowMapper = (resultSet, rowNum) -> {
        Tag tag = new Tag();
        tag.setId(resultSet.getLong("id"));
        tag.setTag(resultSet.getString("tag"));
        return tag;
    };

    public void deleteAll(List<Tag> tags) {
        for (Tag tag : tags) {
            String deleteTag = String.format("DELETE FROM tags WHERE id = %d", tag.getId());
            String deletePost2Tag = String.format("DELETE FROM post2tag WHERE tag_id = %d", tag.getId());
            jdbcTemplate.execute(deletePost2Tag);
            jdbcTemplate.execute(deleteTag);
        }
    }

    public void saveAll(List<String> tags, long postId) {
        for (String tag : tags) {
            save(new Tag(tag), postId);
        }
    }

    public Integer getAllTags() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tags", Integer.class);
    }

    public List<Tag> getTagsIdByName(List<String> tagNames) {
        List<String> tagNames1 = new ArrayList<>();
        tagNames.forEach(tag -> tagNames1.add("'" + tag + "'"));
        return jdbcTemplate.query(
            "SELECT * FROM tags WHERE tag IN (" + StringUtils.join(tagNames1, ", ") + ")",
            tagRowMapper);
    }
}

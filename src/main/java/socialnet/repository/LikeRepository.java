package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Like;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public Integer findCountByPersonId(Long personId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE person_id = ?", Integer.class, personId);
    }

    public List<Like> getLikesByEntityId(long postId) {
        String select = "SELECT * FROM likes WHERE entity_id = ?";
        try {
            return jdbcTemplate.query(select, likeRowMapper, postId);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public Integer save(Like like) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("likes").usingGeneratedKeyColumns("id");

        Map<String, Object> values = new HashMap<>();
        values.put("entity_id", like.getEntityId());
        values.put("time", new Timestamp(System.currentTimeMillis()));
        values.put("person_id", like.getPersonId());
        values.put("type", like.getType());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();
    }

    public void delete(Like like) {
        String delete = "DELETE FROM likes WHERE id = ?";
        jdbcTemplate.update(delete, like.getId());
    }

    public void deleteAll(List<Like> likes) {
        for (Like like : likes) {
            delete(like);
        }
    }

    public Boolean isMyLike(String type, Long entityId, Long personId) {
        return jdbcTemplate.queryForObject(
                "Select case when count(*) > 0 then TRUE else FALSE end case from likes\n" +
                        "Where (type = ? )\n" +
                        "  and ((entity_id = ?) and (person_id = ?)) \n",
                new Object[]{type, entityId, personId},
                Boolean.class);
    }

    private final RowMapper<Like> likeRowMapper = (rs, rowNum) -> {
        Like like = new Like();
        like.setId(rs.getLong("id"));
        like.setType(rs.getString("type"));
        like.setEntityId(rs.getLong("entity_id"));
        like.setTime(rs.getTimestamp("time"));
        like.setPersonId(rs.getLong("person_id"));
        return like;
    };

    public Integer findAllLike(){
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes", Integer.class);
        } catch (EmptyResultDataAccessException ignored){
            return null;
        }
    }
}

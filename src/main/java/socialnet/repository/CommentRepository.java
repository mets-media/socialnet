package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Comment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Comment> findByPostId(Long postId) {
        String select = "SELECT * FROM post_comments WHERE post_id = ?";
        try {
            return jdbcTemplate.query(select, COMMENT_ROW_MAPPER, postId);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Comment> findByPostId(Long postId, int offset, int perPage) {
        String select =
                "SELECT * " +
                "  FROM post_comments " +
                " WHERE is_deleted = false " +
                "   AND post_id = ? " +
                "   AND is_blocked = false " +
                "   AND (parent_id IS NULL OR parent_id = 0) " +
                " ORDER BY time DESC OFFSET ? ROWS LIMIT ?";

        try {
            return jdbcTemplate.query(select, COMMENT_ROW_MAPPER, postId, offset, perPage);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public Long countCommentsByPostId(long postId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) " +
                    "  FROM post_comments pc " +
                    " WHERE pc.post_id = ? " +
                    "   AND pc.is_blocked = false " +
                    "   AND pc.is_deleted = false " +
                    "   AND (pc.parent_id IS NULL OR pc.parent_id = 0)",
                    Long.class,
                    postId);
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            return 0L;
        }
    }

    public Long countByPostId(Long postId) {
        String select = "SELECT COUNT(1) FROM post_comments WHERE is_deleted = false AND post_id = ?";

        try {
            return jdbcTemplate.queryForObject(select, Long.class, postId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<Comment> findByPostIdParentId(Long parentId) {
        String select = "SELECT * FROM post_comments WHERE parent_id = ?";
        try {
            return jdbcTemplate.query(select, COMMENT_ROW_MAPPER, parentId);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public long save(Comment comment) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("post_comments").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("comment_text", comment.getCommentText());
        values.put("is_blocked", comment.getIsBlocked());
        values.put("is_deleted", comment.getIsDeleted());
        values.put("time", comment.getTime());
        values.put("parent_id", comment.getParentId());
        values.put("author_id", comment.getAuthorId());
        values.put("post_id", comment.getPostId());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();
    }

    public Comment findById(Long commentId) {
        String select = "SELECT * FROM post_comments WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(select, COMMENT_ROW_MAPPER, commentId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void updateById(Comment comment, Long commentId) {
        String update = "UPDATE post_comments SET comment_text = ?, is_deleted = ? WHERE id = ?";
        jdbcTemplate.update(update, comment.getCommentText(), comment.getIsDeleted(), commentId);
    }

    public List<Comment> findDeletedPosts() {
        String select = "SELECT * FROM post_comments WHERE is_deleted = true";
        try {
            return jdbcTemplate.query(select, COMMENT_ROW_MAPPER);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public void delete(Comment comment) {
        String delete = "DELETE FROM post_comments WHERE id = ?";
        jdbcTemplate.update(delete, comment.getId());
    }

    public static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setCommentText(rs.getString("comment_text"));
        comment.setIsBlocked(rs.getBoolean("is_blocked"));
        comment.setIsDeleted(rs.getBoolean("is_deleted"));
        comment.setTime(rs.getTimestamp("time"));
        comment.setParentId(rs.getLong("parent_id"));
        comment.setAuthorId(rs.getLong("author_id"));
        comment.setPostId(rs.getLong("post_id"));
        return comment;
    };

    public Integer findByPostIdCount(Long postId) {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post_comments WHERE post_id = ?",
                    Integer.class, postId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void deleteAll(List<Comment> comments) {
        for (Comment comment : comments) {
            delete(comment);
        }
    }
}

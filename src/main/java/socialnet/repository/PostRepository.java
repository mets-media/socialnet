package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import socialnet.model.Post;
import socialnet.model.SearchOptions;
import socialnet.service.PersonService;
import socialnet.service.TagService;

import java.sql.Timestamp;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TagService tagService;

    private final PersonService personService;

    public List<Post> findAll() {
        try {
            return jdbcTemplate.query("SELECT * FROM posts", postRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Post> findAll(int offset, int perPage, long currentTime) {
        try {
            return jdbcTemplate.query(
                "SELECT * FROM posts WHERE is_deleted = false AND time < ? ORDER BY time DESC OFFSET ? ROWS LIMIT ?",
                postRowMapper,
                new Timestamp(currentTime), offset, perPage);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public long getAllCountNotDeleted() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM posts WHERE is_deleted = false", Long.class);
        } catch (EmptyResultDataAccessException | NullPointerException ignored) {
            return 0L;
        }
    }

    public int save(Post post) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("posts").usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("post_text", post.getPostText());
        values.put("time", post.getTime());
        values.put("title", post.getTitle());
        values.put("author_id", post.getAuthorId());
        values.put("is_blocked", post.getIsBlocked());
        values.put("is_deleted", post.getIsDeleted());

        Number id = simpleJdbcInsert.executeAndReturnKey(values);
        return id.intValue();
    }

    public Post findById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM posts WHERE id = ?",
                    postRowMapper,
                    id
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void updateById(int id, Post post) {
        String update = "UPDATE posts SET post_text = ?, title = ?, is_deleted = ?, time_delete = ? WHERE id = ?";
        jdbcTemplate.update(update, post.getPostText(), post.getTitle(), post.getIsDeleted(), post.getTimeDelete(), id);
    }

    public void markAsDeleteById(int id) {
        String update = "UPDATE posts SET is_deleted = true, time_delete = now() WHERE id = ?";
        jdbcTemplate.update(update, id);
    }

    public boolean deleteById(int id) {
        String delete = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(delete, id);
        return true;
    }

    public List<Post> findPostsByUserId(Long userId, Integer offset, Integer perPage) {
        try {
            return jdbcTemplate.query(
                    "select * from posts where author_id = ? order by time desc offset ? rows limit ?",
                    postRowMapper,
                    userId,
                    offset,
                    perPage
            );
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Long countPostsByUserId(Long userId) {
        try {
            return jdbcTemplate.queryForObject(
                "select count(1) from posts where author_id = ?",
                Long.class,
                userId
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Post> findDeletedPosts() {
        String select = "SELECT * FROM posts WHERE is_deleted = true";
        try{
            return jdbcTemplate.query(select, postRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Post> findPostStringSql(SearchOptions searchOptions) {
        try {
            return jdbcTemplate.query(createSqlPost(searchOptions),
                    postRowMapper, searchOptions.getOffset(), searchOptions.getPerPage());
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Integer findPostStringSqlAll(SearchOptions searchOptions) {
        try {
            return jdbcTemplate.queryForObject(createSqlPost(searchOptions),
                    Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private String createSqlPost(SearchOptions searchOptions) {
        StringBuilder sql = new StringBuilder();
        if (Boolean.TRUE.equals(searchOptions.getFlagQueryAll())) {
            sql.append("SELECT DISTINCT COUNT(posts.id) FROM posts");
        } else {
            sql.append("SELECT DISTINCT posts.id, posts.is_blocked, posts.is_deleted, posts.post_text," +
                    " posts.time, posts.time_delete, posts.title, posts.author_id FROM posts");
        }
        if (searchOptions.getText().equals("'")) {
            searchOptions.setText("\"");
        }
        sql.append(createWhereAndTags(searchOptions));
        String sql1;
        if (sql.substring(sql.length() - 5).equals(" AND ")) {
            sql1 = sql.substring(0, sql.length() - 5);
        } else {
            sql1 = sql.toString();
        }
        if (Boolean.TRUE.equals(searchOptions.getFlagQueryAll())) {
            return sql1;
        } else {
            return sql1 + " ORDER BY posts.time DESC OFFSET ? LIMIT ?";
        }
    }

    private Timestamp parseDate(Long str) {
        Date date = new Date(str);
        return new Timestamp(date.getTime());
    }

    public Integer getAllPostByUser(Integer userId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts WHERE author_id = ?",
                Integer.class, userId);
    }

    private String createWhereAndTags(SearchOptions searchOptions) {
        StringBuilder sqlWhere = new StringBuilder();
        String post2TagList = "";
        if (searchOptions.getTags() != null) {
            post2TagList = tagService.getPostByQueryTags(searchOptions.getTags());
            sqlWhere.append(" JOIN post2tag ON posts.id = post2tag.post_id");
        }
        sqlWhere.append(" WHERE is_deleted = false AND ");
        if (searchOptions.getAuthor() != null && !searchOptions.getAuthor().equals("")) {
            sqlWhere.append(personService.searchAuthor(searchOptions));
        }
        sqlWhere.append(searchOptions.getDateFrom() > 0 ? " time > '"
                        + parseDate(searchOptions.getDateFrom()) + "' AND " : "")
                .append(searchOptions.getDateTo() > 0 ? " time < '" + parseDate(searchOptions.getDateTo()) + "' AND " : "");
        if (searchOptions.getTags() != null) {
                sqlWhere.append(post2TagList).append(" AND ");
        }
                sqlWhere.append(!searchOptions.getText().equals("") ? " lower (post_text) LIKE '%" + searchOptions.getText()
                        .toLowerCase() + "%'" : "");

        return sqlWhere.toString();
    }

    private final RowMapper<Post> postRowMapper = (resultSet, rowNum) -> {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setIsBlocked(resultSet.getBoolean("is_blocked"));
        post.setIsDeleted(resultSet.getBoolean("is_deleted"));
        post.setPostText(resultSet.getString("post_text"));
        post.setTime(resultSet.getTimestamp("time"));
        post.setTimeDelete(resultSet.getTimestamp("time_delete"));
        post.setTitle(resultSet.getString("title"));
        post.setAuthorId(resultSet.getLong("author_id"));

        return post;
    };
}

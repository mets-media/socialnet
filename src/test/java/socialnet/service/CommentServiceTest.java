package socialnet.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import socialnet.BasicTest;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.security.jwt.JwtUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static socialnet.repository.CommentRepository.COMMENT_ROW_MAPPER;

@ContextConfiguration(initializers = {CommentServiceTest.Initializer.class})
@Sql(scripts = {"/sql/clear_tables.sql", "/sql/posts_controller_test.sql", "/sql/create_random_comments.sql", "/sql/create_random_likes.sql"})
@SqlMergeMode(MERGE)
public class CommentServiceTest extends BasicTest {
    private static final Long POST_ID_1 = 1L;
    private static final String USER_EMAIL = "user1@email.com";
    @Autowired
    private CommentService commentService;
    @MockBean
    private JwtUtils jwtUtils;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:12.14");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRES_CONTAINER.getUsername(),
                    "spring.datasource.password=" + POSTGRES_CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    @DisplayName("Поднятие контекста.")
    void contextLoads() {
        assertThat(POSTGRES_CONTAINER.isRunning()).isTrue();
        assertThat(jwtUtils).isNotNull();
    }

    @Test
    void getComments() {
        var retVal = commentService.getComments(POST_ID_1, 0, 20);
        assertNotNull(retVal);
        assertEquals(2, retVal.getTotal());
        List<CommentRs> data = retVal.getData();
        assertEquals("Комментарий №1 к посту #1", data.get(0).getCommentText());
        assertEquals("Комментарий №2 к посту #1", data.get(1).getCommentText());
    }

    @Test
    @Sql(statements = "INSERT INTO person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) VALUES (false, true, true, true, false, false, false)")
    void createComment() {
        doReturn(USER_EMAIL).when(jwtUtils).getUserEmail(anyString());
        var commentRequest = new CommentRq();
        commentRequest.setCommentText("comment #1");
        commentRequest.setParentId(1);
        var retVal = commentService.createComment(commentRequest, POST_ID_1, "anything");
        assertNotNull(retVal);
    }

    @Test
    void getToDTODetails() {
        var postId = 1L;
        var comment = jdbcTemplate.queryForObject("SELECT * FROM post_comments WHERE id = 1", COMMENT_ROW_MAPPER);
        var commentId = comment.getId();
        var retVal = commentService.getToDTODetails(postId, comment, commentId);
        assertNotNull(retVal);
    }

    @Test
    void editComment() {
        doReturn(USER_EMAIL).when(jwtUtils).getUserEmail(anyString());
        var commentRq = new CommentRq();
        commentRq.setCommentText("new text");
        var retVal = commentService.editComment("anything", 1L, 1L, commentRq);
        assertNotNull(retVal);
    }

    @Test
    void deleteComment() {
        var retVal = commentService.deleteComment(1L, 1L);
        assertNotNull(retVal);
        assertTrue(retVal.getData().getIsDeleted());
    }

    @Test
    @Sql(statements = "UPDATE post_comments SET is_deleted = true")
    void hardDeleteComments() {
        commentService.hardDeleteComments();
        var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post_comments", Long.class);
        assertEquals(0, count);
    }

    @Test
    @Sql(statements = "UPDATE post_comments SET is_deleted = true WHERE id = 1")
    void recoverComment() {
        var retVal = commentService.recoverComment(1L, 1L);
        assertNotNull(retVal);
        var comment = jdbcTemplate.queryForObject("SELECT * FROM post_comments WHERE id = 1", COMMENT_ROW_MAPPER);
        assertFalse(comment.getIsDeleted());
    }
}
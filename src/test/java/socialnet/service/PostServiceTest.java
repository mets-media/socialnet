package socialnet.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import socialnet.api.request.PostRq;
import socialnet.api.response.CommentRs;
import socialnet.api.response.PostRs;
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.exception.EntityNotFoundException;
import socialnet.repository.PostRepository;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.security.jwt.JwtUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { PostServiceTest.Initializer.class })
@Sql(scripts = "/sql/clear_tables.sql")
@Sql(scripts = "/sql/post_service.sql")
@SqlMergeMode(MERGE)
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
@MockBean(KafkaConsumerConfig.class)
@MockBean(KafkaProducerConfig.class)
@MockBean(KafkaTopicConfig.class)
@MockBean(KafkaService.class)
class PostServiceTest {
    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @MockBean
    JwtUtils jwtUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static final String USER_EMAIL = "user1@email.com";
    static final String TOKEN = "token";

    @Container
    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.14");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + container.getJdbcUrl(),
                "spring.datasource.username=" + container.getUsername(),
                "spring.datasource.password=" + container.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeEach
    void setUp() {
        doReturn(USER_EMAIL).when(jwtUtils).getUserEmail(anyString());
    }

    @Test
    @DisplayName("Поднятие контекста")
    void contextLoads() {
        assertThat(container.isRunning()).isTrue();
        assertThat(postService).isNotNull();
        assertThat(postRepository).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    @Test
    @DisplayName("Все новости")
    void getFeeds() {
        var actual = postService.getFeeds(TOKEN, 0, 10);

        assertThat(actual.getData())
            .hasSize(3)
            .extracting(PostRs::getTitle)
            .containsExactlyInAnyOrder("Post title #2", "Post title #3", "Post title #4");
    }

    @Test
    @DisplayName("Получение подробной информации о посте")
    void getDetails() {
        var actual = postService.getDetails(1, 3, TOKEN);

        assertEquals("User", actual.getAuthor().getFirstName());
        assertEquals("Testovich", actual.getAuthor().getLastName());
        assertThat(actual.getComments())
            .hasSize(1)
            .extracting(CommentRs::getCommentText)
            .containsExactlyInAnyOrder("Комментарий к посту #3");
    }

    @Test
    @DisplayName("Создание поста")
    void createPost() {
        String expectedTitle = "Тест на создание поста";
        String expectedText = "Да, это тест.";

        PostRq post = new PostRq();
        post.setTitle(expectedTitle);
        post.setPostText(expectedText);

        var actual = postService.createPost(post, 1, System.currentTimeMillis(), TOKEN);

        assertEquals("User", actual.getData().getAuthor().getFirstName());
        assertEquals("Testovich", actual.getData().getAuthor().getLastName());
        assertEquals(expectedTitle, actual.getData().getTitle());
        assertEquals(expectedText, actual.getData().getPostText());
    }

    @Test
    @DisplayName("Мягкое удаление поста")
    void markAsDelete() {
        postService.markAsDelete(2, TOKEN);
        var actual = postRepository.findById(2);

        assertTrue(actual.getIsDeleted());
    }

    @Test
    @DisplayName("Восстановление поста")
    void recoverPost() {
        postService.recoverPost(2, TOKEN);
        var actual = postRepository.findById(2);

        assertFalse(actual.getIsDeleted());
        assertNull(actual.getTimeDelete());
    }

    @Test
    @DisplayName("Жёсткое удаление постов")
    void hardDeletingPosts() {
        postService.markAsDelete(3, TOKEN);
        postService.hardDeletingPosts();

        int cntPosts = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM posts", Integer.class);
        int cntComments = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM post_comments", Integer.class);
        int cntTags = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM tags", Integer.class);
        int cntPost2Tags = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM post2tag", Integer.class);
        int cntLikes = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM likes", Integer.class);

        assertEquals(2, cntPosts);
        assertEquals(0, cntComments);
        assertEquals(0, cntTags);
        assertEquals(0, cntPost2Tags);
        assertEquals(0, cntLikes);
    }

    @Test
    @DisplayName("Обновление поста")
    void updatePost() {
        String expectedTitle = "Updated title";
        String expectedText = "Updated text";

        PostRq postRq = new PostRq();
        postRq.setTitle(expectedTitle);
        postRq.setPostText(expectedText);

        postService.updatePost(3, postRq, TOKEN);

        var actual = postService.getPostById(3, TOKEN);

        assertEquals(expectedTitle, actual.getData().getTitle());
        assertEquals(expectedText, actual.getData().getPostText());
    }

    @Test
    @DisplayName("Получение постов определённого автора")
    void getFeedsByAuthorId() {
        var actual = postService.getFeedsByAuthorId(2L, TOKEN, 0, 10);

        assertEquals(1, actual.getData().size());
        assertEquals("Post title #4", actual.getData().get(0).getTitle());
    }

    @Test
    @DisplayName("Получение поста с несуществующим ID")
    void getPostById() {
        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(666, TOKEN));
    }
}

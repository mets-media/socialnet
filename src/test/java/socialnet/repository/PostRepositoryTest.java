package socialnet.repository;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.model.Post;
import socialnet.model.SearchOptions;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.service.KafkaService;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { PostRepositoryTest.Initializer.class })
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
class PostRepositoryTest {
    @Autowired
    PostRepository postRepository;

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

    @Test
    @DisplayName("Поднятие контекста")
    void contextLoads() {
        assertThat(container.isRunning()).isTrue();
        assertThat(postRepository).isNotNull();
    }

    @Test
    @DisplayName("Поиск всех постов")
    void findAll() {
        assertEquals(4, postRepository.findAll().size());
    }

    @Test
    @DisplayName("Поиск всех постов по времени")
    void findAllByTime() {
        assertEquals(1, postRepository.findAll(0, 10, System.currentTimeMillis() - 3600000).size());
    }

    @Test
    @DisplayName("Получение всех неудалённых постов")
    void getAllCountNotDeleted() {
        assertEquals(3, postRepository.getAllCountNotDeleted());
    }

    @Test
    @DisplayName("Сохранение поста")
    void save() {
        Post post = new Post();
        post.setTitle("Title");
        post.setPostText("Text");
        post.setAuthorId(1L);
        post.setTime(null);
        post.setIsBlocked(false);
        post.setIsDeleted(false);

        postRepository.save(post);

        assertEquals(5, postRepository.findAll().size());
    }

    @Test
    @DisplayName("Получение поста по ID")
    void findById() {
        var actual = postRepository.findById(1);

        assertNotNull(actual);
        assertEquals(1, actual.getId());
    }

    @Test
    @DisplayName("Получение поста по несуществующему ID")
    void findByNotExistsId() {
        assertNull(postRepository.findById(666));
    }

    @Test
    @DisplayName("Обновление поста по ID")
    void updateById() {
        String expectedTitle = "Обновлённый пост";
        String expectedText = "Обновлённый текст";

        Post post = new Post();
        post.setTitle(expectedTitle);
        post.setPostText(expectedText);
        post.setIsDeleted(false);
        post.setTimeDelete(new Timestamp(System.currentTimeMillis()));

        postRepository.updateById(4, post);
        var actual = postRepository.findById(4);

        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedText, actual.getPostText());
    }

    @Test
    @DisplayName("Мягкое удаление поста")
    void markAsDeleteById() {
        postRepository.markAsDeleteById(2);
        var actual = postRepository.findById(2);

        assertTrue(actual.getIsDeleted());
    }

    @Test
    @DisplayName("Удаление поста")
    void deleteById() {
        postRepository.deleteById(1);

        assertNull(postRepository.findById(1));
    }

    @Test
    @DisplayName("Поиск постов по ID автора")
    void findPostsByUserId() {
        var actual = postRepository.findPostsByUserId(1L, 0, 10);

        assertEquals(3, actual.size());
        assertEquals(1, actual.get(0).getAuthorId());
        assertEquals(1, actual.get(1).getAuthorId());
        assertEquals(1, actual.get(2).getAuthorId());
    }

    @Test
    @DisplayName("Количество постов определённого автора")
    void countPostsByUserId() {
        assertEquals(3, postRepository.countPostsByUserId(1L));
    }

    @Test
    @DisplayName("Поиск удалённых постов")
    void findDeletedPosts() {
        assertEquals(1, postRepository.findDeletedPosts().size());
    }

    @Test
    @DisplayName("Поиск постов по тексту")
    void findPostStringSqlText() {
        SearchOptions so = SearchOptions.builder()
            .jwtToken("token")
            .author("")
            .dateFrom(0L)
            .dateTo(0L)
            .offset(0)
            .perPage(20)
            .tags(null)
            .text("пост")
            .build();

        var actual = postRepository.findPostStringSql(so);

        assertThat(actual)
            .hasSize(2)
            .extracting(Post::getTitle)
            .containsExactlyInAnyOrder("Post title #2", "Post title #4");
    }

    @Test
    @DisplayName("Поиск постов по тегам")
    void findPostStringSqlTags() {
        SearchOptions so = SearchOptions.builder()
            .jwtToken("token")
            .author("")
            .dateFrom(0L)
            .dateTo(0L)
            .offset(0)
            .perPage(20)
            .tags(new String[] { "post3tag", "onemoretag" })
            .text("")
            .build();

        var actual = postRepository.findPostStringSql(so);

        assertThat(actual)
            .hasSize(1)
            .extracting(Post::getTitle)
            .containsExactlyInAnyOrder("Post title #3");
    }

    @Test
    @DisplayName("Поиск постов по автору")
    void findPostStringSqlAuthor() {
        SearchOptions so = SearchOptions.builder()
            .jwtToken("token")
            .author("testovich")
            .dateFrom(0L)
            .dateTo(0L)
            .offset(0)
            .perPage(20)
            .tags(null)
            .text("")
            .build();

        var actual = postRepository.findPostStringSql(so);

        assertThat(actual)
            .hasSize(2)
            .extracting(Post::getTitle)
            .containsExactlyInAnyOrder("Post title #2", "Post title #3");
    }

    @Test
    @DisplayName("Поиск постов по автору и тексту")
    void findPostStringSqlAuthorText() {
        SearchOptions so = SearchOptions.builder()
            .jwtToken("token")
            .author("testovich")
            .dateFrom(0L)
            .dateTo(0L)
            .offset(0)
            .perPage(20)
            .tags(null)
            .text("ъъ")
            .build();

        var actual = postRepository.findPostStringSql(so);

        assertThat(actual)
            .hasSize(1)
            .extracting(Post::getTitle)
            .containsExactlyInAnyOrder("Post title #3");
    }

    @Test
    @DisplayName("Поиск постов по дате")
    void findPostStringSqlDate() {
        SearchOptions so = SearchOptions.builder()
            .jwtToken("token")
            .author("")
            .dateFrom(0L)
            .dateTo(System.currentTimeMillis() - 3600000)
            .offset(0)
            .perPage(20)
            .tags(null)
            .text("")
            .build();

        var actual = postRepository.findPostStringSql(so);

        assertThat(actual)
            .hasSize(1)
            .extracting(Post::getTitle)
            .containsExactlyInAnyOrder("Post title #2");
    }

    @Test
    @DisplayName("Поиск всех постов с параметрами")
    void findPostStringSqlAll() {
        SearchOptions so = SearchOptions.builder()
            .jwtToken("token")
            .author("testovich")
            .dateFrom(0L)
            .dateTo(0L)
            .offset(0)
            .perPage(20)
            .tags(null)
            .text("")
            .flagQueryAll(true)
            .build();

        assertEquals(2, postRepository.findPostStringSqlAll(so));
    }

    @Test
    @DisplayName("Поиск всех постов автора")
    void getAllPostByUser() {
        assertEquals(3, postRepository.getAllPostByUser(1));
    }
}

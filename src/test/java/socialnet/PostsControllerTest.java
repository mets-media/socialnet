package socialnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import socialnet.api.request.PostRq;
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.KafkaService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { PostsControllerTest.Initializer.class })
@Sql({ "/sql/posts_controller_test.sql" })
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
@MockBean(KafkaConsumerConfig.class)
@MockBean(KafkaProducerConfig.class)
@MockBean(KafkaTopicConfig.class)
@MockBean(KafkaService.class)
public class PostsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String TEST_EMAIL = "user1@email.com";

    @ClassRule
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

    public RequestPostProcessor authorization() {
        return request -> {
            request.addHeader("authorization", jwtUtils.generateJwtToken(TEST_EMAIL));
            return request;
        };
    }

    @Test
    @DisplayName("Загрузка контекста")
    @Transactional
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    @Test
    @DisplayName("Неавторизованный пользователь")
    @Transactional
    public void accessDenied() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1"))
            .andExpect(unauthenticated())
            .andDo(print());
    }

    @Test
    @DisplayName("Получение поста по существующему ID")
    @Transactional
    public void getPostByExistsId() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1").with(authorization()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data.id", is(1)))
            .andExpect(jsonPath("$.data.title", is("Title #1")))
            .andDo(print());
    }

    @Test
    @DisplayName("Обновление поста по ID")
    @Transactional
    public void updatePostById() throws Exception {
        String expectedText = "Some text updated";

        PostRq postRq = new PostRq();
        postRq.setTitle("Title #1");
        postRq.setPostText(expectedText);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String content = ow.writeValueAsString(postRq);

        mockMvc
            .perform(
                put("/api/v1/post/1")
                    .with(authorization())
                    .contentType("application/json")
                    .accept("application/json")
                    .content(content)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data.post_text", is(expectedText)))
            .andDo(print());
    }

    @Test
    @DisplayName("Удаление поста по ID")
    @Transactional
    public void deletePostById() throws Exception {
        mockMvc
            .perform(
                delete("/api/v1/post/1")
                    .with(authorization())
                    .contentType("application/json")
                    .accept("application/json")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data.id", is(1)))
            .andDo(print());

        boolean isError = false;
        boolean isDeleted = false;

        try {
            isDeleted = jdbcTemplate.queryForObject(
                "select is_deleted from posts where id = 1",
                Boolean.class
            );
        } catch (Exception ignored) {
            isError = true;
        }

        assertFalse(isError);
        assertTrue(isDeleted);
    }

    @Test
    @DisplayName("Восстановление поста по ID")
    @Transactional
    public void recoverPostById() throws Exception {
        mockMvc
            .perform(
                put("/api/v1/post/1/recover")
                    .with(authorization())
                    .contentType("application/json")
                    .accept("application/json")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data.id", is(1)))
            .andDo(print());

        boolean isError = false;
        boolean isDeleted = false;

        try {
            isDeleted = jdbcTemplate.queryForObject(
                "select is_deleted from posts where id = 1",
                Boolean.class
            );
        } catch (Exception ignored) {
            isError = true;
        }

        assertFalse(isError);
        assertFalse(isDeleted);
    }

    @Test
    @DisplayName("Создание поста")
    @Transactional
    public void createPost() throws Exception {
        String expectedTitle = "Title #19";
        String expectedText = "Some text";

        PostRq postRq = new PostRq();
        postRq.setTitle(expectedTitle);
        postRq.setPostText(expectedText);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String content = ow.writeValueAsString(postRq);

        mockMvc
            .perform(
                post("/api/v1/users/1/wall")
                    .with(authorization())
                    .contentType("application/json")
                    .accept("application/json")
                    .content(content)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data.author.id", is(1)))
            .andExpect(jsonPath("$.data.title", is(expectedTitle)))
            .andExpect(jsonPath("$.data.post_text", is(expectedText)))
            .andDo(print());
    }

    @Test
    @DisplayName("Создание поста со спецсимволами")
    @Transactional
    public void createPostWithBadContent() throws Exception {
        String expectedTitle = "Title #19";
        String expectedText = "'";

        PostRq postRq = new PostRq();
        postRq.setTitle(expectedTitle);
        postRq.setPostText(expectedText);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String content = ow.writeValueAsString(postRq);

        mockMvc
            .perform(
                post("/api/v1/users/1/wall")
                    .with(authorization())
                    .contentType("application/json")
                    .accept("application/json")
                    .content(content)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data.author.id", is(1)))
            .andExpect(jsonPath("$.data.title", is(expectedTitle)))
            .andDo(print());
    }

    @Test
    @DisplayName("Получение всех постов с пагинацией")
    @Transactional
    public void getPostsWithPagination() throws Exception {
        mockMvc
            .perform(
                get("/api/v1/users/1/wall")
                    .with(authorization())
                    .param("offset", "5")
                    .param("perPage", "5")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(5)))
            .andExpect(jsonPath("$.data[0].id", is(6)))
            .andExpect(jsonPath("$.data[4].id", is(10)))
            .andExpect(jsonPath("$.data[0].author.id", is(1)))
            .andExpect(jsonPath("$.data[1].author.id", is(1)))
            .andExpect(jsonPath("$.data[2].author.id", is(1)))
            .andExpect(jsonPath("$.data[3].author.id", is(1)))
            .andExpect(jsonPath("$.data[4].author.id", is(1)))
            .andDo(print());
    }

    @Test
    @DisplayName("Получение всех постов")
    @Transactional
    public void getPosts() throws Exception {
        mockMvc
            .perform(get("/api/v1/users/1/wall").with(authorization()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(10)))
            .andExpect(jsonPath("$.data[0].author.id", is(1)))
            .andExpect(jsonPath("$.data[1].author.id", is(1)))
            .andExpect(jsonPath("$.data[2].author.id", is(1)))
            .andExpect(jsonPath("$.data[3].author.id", is(1)))
            .andExpect(jsonPath("$.data[4].author.id", is(1)))
            .andExpect(jsonPath("$.data[5].author.id", is(1)))
            .andExpect(jsonPath("$.data[6].author.id", is(1)))
            .andExpect(jsonPath("$.data[7].author.id", is(1)))
            .andExpect(jsonPath("$.data[8].author.id", is(1)))
            .andExpect(jsonPath("$.data[9].author.id", is(1)))
            .andDo(print());
    }

    @Test
    @DisplayName("Поиск постов по тексту")
    @Transactional
    public void getPostsByText() throws Exception {
        mockMvc
            .perform(
                get("/api/v1/post")
                    .with(authorization())
                    .param("text", "fucking post")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andDo(print());
    }

    @Test
    @DisplayName("Проверка поиска на спецсимволы")
    @Transactional
    public void getPostsByBadText() throws Exception {
        mockMvc
            .perform(
                get("/api/v1/post")
                    .with(authorization())
                    .param("text", "'")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andDo(print());
    }

    @Test
    @DisplayName("Список новостей не должен содержать удалённые посты")
    @Transactional
    public void getFeeds() throws Exception {
        mockMvc
            .perform(get("/api/v1/feeds").with(authorization()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(17)))
            .andDo(print());
    }

    @Test
    @DisplayName("Получение всех новостей с пагинацией")
    @Transactional
    public void getFeedsWithPagination() throws Exception {
        mockMvc
            .perform(
                get("/api/v1/feeds")
                    .with(authorization())
                    .param("offset", "0")
                    .param("perPage", "5")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(5)))
            .andExpect(jsonPath("$.total", is(17)))
            .andDo(print());
    }
}

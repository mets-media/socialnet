package socialnet;


import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
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
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.KafkaService;

import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {NotificationsControllerTest.Initializer.class})
@Sql(value = {"/sql/create-notifications-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
@MockBean(KafkaConsumerConfig.class)
@MockBean(KafkaProducerConfig.class)
@MockBean(KafkaTopicConfig.class)
@MockBean(KafkaService.class)
public class NotificationsControllerTest {
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
    @DisplayName("Получение всех нотификаций")
    @Transactional
    public void getAllNotifications() throws Exception {
        mockMvc
                .perform(get("/api/v1/notifications").with(authorization()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andDo(print());
    }
    @Test
    @DisplayName("Прочтение всех нотификаций")
    @Transactional
    public void putAllNotifications() throws Exception {
        mockMvc
                .perform(put("/api/v1/notifications").with(authorization()).param("all","true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(print());
        Integer count = jdbcTemplate.queryForObject("select COUNT(*) from notifications where person_id = 1 and " +
                "is_read = true", Integer.class);
        Assert.assertEquals(2, Objects.requireNonNull(count).intValue());
    }
    @Test
    @DisplayName("Прочтение нотификации по ID")
    @Transactional
    public void putNotificationById() throws Exception {
        mockMvc
                .perform(put("/api/v1/notifications").with(authorization()).param("id","1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(print());
        Integer count = jdbcTemplate.queryForObject("select COUNT(*) from notifications where person_id = 1 and " +
                "is_read = true", Integer.class);
        Assert.assertEquals(1, Objects.requireNonNull(count).intValue());
    }
}

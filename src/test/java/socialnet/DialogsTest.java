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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import socialnet.api.request.DialogUserShortListDto;
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.repository.MessageRepository;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.KafkaService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { DialogsTest.Initializer.class })
@Sql({ "/sql/posts_controller_test.sql" })
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
@MockBean(KafkaConsumerConfig.class)
@MockBean(KafkaProducerConfig.class)
@MockBean(KafkaTopicConfig.class)
@MockBean(KafkaService.class)
public class DialogsTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

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
    @DisplayName("recover comment by id")
    @Transactional
    public void getDialogs() throws Exception {

        this.mockMvc
                .perform(get("/api/v1/dialogs")
                        .with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("get count of unread messages")
    @Transactional
    public void getUnreadedMessages() throws Exception {

        this.mockMvc
                .perform(get("/api/v1/dialogs/unreaded")
                        .with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("get messages from dialog")
    @Transactional
    public void getMessagesFromDialog() throws Exception {

        this.mockMvc
                .perform(get("/api/v1/dialogs/1/messages")
                        .with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("read messages in dialog")
    @Transactional
    public void readMessagesInDialog() throws Exception {

        this.mockMvc
                .perform(put("/api/v1/dialogs/1/read")
                        .with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Transactional
    public void startDialog() throws Exception {

        Long expectUserId = 1L;
        List<Long> expectUserIds = new ArrayList<>();

        DialogUserShortListDto dialogUserShortListDto = new DialogUserShortListDto();
        dialogUserShortListDto.setUserId(expectUserId);
        dialogUserShortListDto.setUserIds(expectUserIds);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String content = ow.writeValueAsString(dialogUserShortListDto);

        this.mockMvc
                .perform(post("/api/v1/dialogs")
                        .with(authorization())
                        .contentType("application/json")
                        .accept("application/json")
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}

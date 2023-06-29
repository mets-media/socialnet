package socialnet;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.controller.FriendsController;
import socialnet.model.Friendships;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.FriendsShipsRepository;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.KafkaService;
import socialnet.service.PersonService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { FriendsTest.Initializer.class })
@Sql(value = {"/sql/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/create-friendships-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
@MockBean(KafkaConsumerConfig.class)
@MockBean(KafkaProducerConfig.class)
@MockBean(KafkaTopicConfig.class)
@MockBean(KafkaService.class)
public class FriendsTest {
    @Autowired
    private FriendsController friendsController;

    @Autowired
    private FriendsShipsRepository friendsShipsRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PersonService personService;

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
        assertThat(friendsController).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(personService).isNotNull();
        assertThat(mockMvc).isNotNull();
        assertThat(friendsShipsRepository).isNotNull();
    }

    @Test
    @Transactional
    public void getFriendsTest() throws Exception{
        this.mockMvc
                .perform(get("/api/v1/friends").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].email", is("jjewell55@ebay.com")))
                .andExpect(jsonPath("$.data[1].email", is("kutting1@eventbrite.com")))
                .andExpect(jsonPath("$.data[2].email", is("user2@email.com")))
                .andReturn();
    }

    @Test
    @Transactional
    public void getOutgoingRequests() throws Exception {
        this.mockMvc
            .perform(get("/api/v1/friends/outgoing_requests").with(authorization()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data[0].email", is("jjewell5@ebay.com")))
            .andExpect(jsonPath("$.data[1].email", is("fbrisset4@zimbio.com")))
            .andReturn();
    }
    @Test
    @Transactional
    public void blocksUser() throws Exception {

        String startValue = friendsShipsRepository.findFriend(1L, 3L).getStatusName().toString();

        this.mockMvc
            .perform(post("/api/v1/friends/block_unblock/3").with(authorization()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String newValue = friendsShipsRepository.getFriendStatusBlocked(1L, 3L).getStatusName().toString();
        assertThat(!startValue.equals(newValue)).isTrue();
    }
    @Test
    public void getRecommendedFriendsTest() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(get("/api/v1/friends/recommendations").with(authorization()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.size()", is(9)))
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        boolean firstCondition = response.contains("fbrisset4@zimbio.com");
        boolean secondCondition = response.contains("jjewell5@ebay.com");
        boolean thirdCondition = !response.contains(TEST_EMAIL);

        assertThat(firstCondition && secondCondition && thirdCondition).isTrue();
    }
    @Test
    public void getPotentialFriendsTest() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(get("/api/v1/friends/request").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        boolean firstCondition = response.contains("nwickey2@ibm.com");
        boolean secondCondition = response.contains("dsuermeiers3@gmpg.org");
        boolean thirdCondition = !response.contains(TEST_EMAIL);

        assertThat(firstCondition && secondCondition && thirdCondition).isTrue();
    }
    @Test
    public void addFriendTest() throws Exception {
        FriendshipStatusTypes startStatus = friendsShipsRepository.findFriend(1L, 6L).getStatusName();

        this.mockMvc
                .perform(post("/api/v1/friends/request/6").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        FriendshipStatusTypes endStatus = friendsShipsRepository.findFriend(1L, 6L).getStatusName();

        assertThat(startStatus.equals(endStatus)).isFalse();
    }
    @Test
    public void deleteFriendsRequestTest() throws Exception {
        FriendshipStatusTypes startStatus = friendsShipsRepository.findRequest(1L, 5L).getStatusName();

        this.mockMvc
                .perform(delete("/api/v1/friends/request/5").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Friendships endFriendship = friendsShipsRepository.findRequest(1L, 5L);
        assertThat(!startStatus.equals(FriendshipStatusTypes.DECLINED)
                &&
                endFriendship == null)
                .isTrue();
    }
    @Test
    public void sendFriendsRequestTest() throws Exception {
        this.mockMvc
                .perform(post("/api/v1/friends/10").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        FriendshipStatusTypes endStatus = friendsShipsRepository.findRequest(1L, 10L).getStatusName();
        assertThat(endStatus.equals(FriendshipStatusTypes.REQUEST)).isTrue();
    }
    @Test
    public void deleteFriendTest() throws Exception {
        FriendshipStatusTypes startStatus = friendsShipsRepository.findFriend(1L, 2L).getStatusName();
        this.mockMvc
                .perform(delete("/api/v1/friends/2").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Friendships endFriendship = friendsShipsRepository.findFriend(1L, 2L);
        boolean firstCondition = startStatus.equals(FriendshipStatusTypes.FRIEND);
        boolean secondCondition = endFriendship == null;
        assertThat(firstCondition && secondCondition).isTrue();
    }
}

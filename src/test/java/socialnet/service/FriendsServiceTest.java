package socialnet.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.repository.FriendsShipsRepository;
import socialnet.security.jwt.JwtUtils;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { FriendsServiceTest.Initializer.class })
@Sql("/sql/clear_tables.sql")
@Sql("/sql/friendsServiceTest-before.sql")
@SqlMergeMode(MERGE)
public class FriendsServiceTest {
        @Autowired
        private FriendsService friendsService;

        @Autowired
        private FriendsShipsRepository friendsShipsRepository;

        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private JdbcTemplate jdbcTemplate;

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

    private String getToken(String email) {
        return jwtUtils.generateJwtToken(email);
    }

        @Test
        @DisplayName("Context load")
    void contextLoads() {
        assertThat(container.isRunning()).isTrue();
        assertThat(friendsService).isNotNull();
        assertThat(friendsShipsRepository).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    @Test
    @DisplayName("Get friends")
    void getFriends() {
        String email = "user1@email.com";
        CommonRs<List<PersonRs>> commonRs = friendsService.getFriends(getToken(email), 0, 20);
        assertThat(commonRs.getData().size()).isSameAs(3);
        List<PersonRs> friends = commonRs.getData();
        List<String> friendsEmails = friends.stream().map(PersonRs::getEmail).collect(Collectors.toList());
        assertThat(friendsEmails).containsAll(List.of(
                "user2@email.com",
                "kutting1@eventbrite.com",
                "jjewell55@ebay.com"));
    }

    @Test
    @DisplayName("Block User")
    void blockUserTest() {
        String email = "user1@email.com";
        String firstActualFriendStatus = friendsService.getFriendStatus(1, 2);
        String secondActualFriendStatus = friendsService.getFriendStatus(1, 8);

        friendsService.userBlocks(getToken(email), 2);
        friendsService.userBlocks(getToken(email), 8);

        String firstFinalFriendStatus = friendsService.getFriendStatus(1, 2);
        String secondFinalFriendStatus = friendsService.getFriendStatus(1, 8);

        assertThat(firstActualFriendStatus.equals("FRIEND")
                && secondActualFriendStatus.equals("BLOCKED"))
                .isTrue();
        assertThat(firstFinalFriendStatus.equals("BLOCKED")
                && secondFinalFriendStatus.equals("FRIEND"))
                .isTrue();
    }

    @Test
    @DisplayName("Recommended by city")
    void getRecommendedFriendsByCityTest() {
        String email = "user2@email.com";
        CommonRs<List<PersonRs>> recommendations = friendsService.getRecommendedFriends(getToken(email));

        List<String> recommendationsEmails = recommendations.getData()
                .stream()
                .map(PersonRs::getEmail)
                .collect(Collectors.toList());

        assertThat(recommendationsEmails).containsAll(List.of(
                "fdresser2n@google.com.au",
                "enovakovic2m@intel.com"));
    }
}

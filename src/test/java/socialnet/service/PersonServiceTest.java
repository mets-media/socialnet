package socialnet.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import socialnet.BasicTest;
import socialnet.api.request.EmailRq;
import socialnet.api.request.LoginRq;
import socialnet.api.request.PasswordSetRq;
import socialnet.api.request.UserRq;
import socialnet.api.response.PersonSettingsRs;
import socialnet.exception.EmptyEmailException;
import socialnet.security.jwt.JwtUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static socialnet.repository.PersonRepository.PERSON_ROW_MAPPER;

@ContextConfiguration(initializers = {PersonServiceTest.Initializer.class})
@Sql(scripts = "/sql/clear_tables.sql")
@SqlMergeMode(MERGE)
public class PersonServiceTest extends BasicTest {
    private static final String USER_EMAIL = "user@email.com";
    private static final String USER_EMAIL_2 = "another@email.com";
    private static final String PASSWORD_CORRECT = "12345678";
    private static final String PASSWORD_INCORRECT = "INVALID_PASSWORD";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PersonService personService;
    @MockBean
    private JwtUtils jwtUtils;

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

    @BeforeEach
    void setUp() {
        doReturn(USER_EMAIL).when(jwtUtils).getUserEmail(anyString());
    }

    @Test
    @DisplayName("Поднятие контекста.")
    void contextLoads() {
        assertThat(POSTGRES_CONTAINER.isRunning()).isTrue();
        assertThat(jwtUtils).isNotNull();
        assertThat(personService).isNotNull();
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void delete() {
        var result = personService.delete("anything");
        assertNotNull(result);
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons WHERE email = ?", PERSON_ROW_MAPPER, USER_EMAIL);
        assertTrue(person.getIsDeleted());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    @Sql(statements = "UPDATE persons SET is_deleted = true WHERE email = '" + USER_EMAIL + "'")
    void recover() {
        var result = personService.recover("anything");
        assertNotNull(result);
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons WHERE email = ?", PERSON_ROW_MAPPER, USER_EMAIL);
        assertFalse(person.getIsDeleted());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getLogin() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "socialNet");
        doCallRealMethod().when(jwtUtils).generateJwtToken(anyString());
        var loginRq = new LoginRq();
        loginRq.setEmail(USER_EMAIL);
        loginRq.setPassword(PASSWORD_CORRECT);
        var retVal = personService.getLogin(loginRq);
        assertNotNull(retVal.getData());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getMyProfile() {
        var retVal = personService.getMyProfile("anystring");
        assertNotNull(retVal.getData());
        var personRs = retVal.getData();
        assertTrue(personRs.getOnline());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void setNewEmail() {
        var emailRq = new EmailRq();
        emailRq.setEmail(USER_EMAIL_2);
        emailRq.setSecret("anything");
        var registerRs = personService.setNewEmail(emailRq);
        assertNotNull(registerRs);
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons", PERSON_ROW_MAPPER);
        assertEquals(USER_EMAIL_2, person.getEmail());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void resetPassword() {
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons", PERSON_ROW_MAPPER);
        var oldPassword = person.getPassword();
        var passwordSetRq = new PasswordSetRq();
        passwordSetRq.setPassword("new_password");
        var retVal = personService.resetPassword("anything", passwordSetRq);
        assertNotNull(retVal);
        person = jdbcTemplate.queryForObject("SELECT * FROM persons", PERSON_ROW_MAPPER);
        var newPassword = person.getPassword();
        assertNotEquals(oldPassword, newPassword);
    }

    @Test
    void getLogout() {
        var retVal = personService.getLogout();
        assertNotNull(retVal);
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getUserById() {
        var retVal = personService.getUserById("anything", 1);
        assertNotNull(retVal);
    }

    @Test
    void getAuthPerson() {
        assertThrows(NullPointerException.class, () -> personService.getAuthPerson());
    }

    @Test
    void getAuthPersonId() {
        assertThrows(NullPointerException.class, () -> personService.getAuthPersonId());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void checkLoginAndPassword() {
        var person = personService.checkLoginAndPassword(USER_EMAIL, PASSWORD_CORRECT);
        assertNotNull(person);
        assertEquals("Chris", person.getFirstName());
        var exception = assertThrows(EmptyEmailException.class,
                () -> personService.checkLoginAndPassword(USER_EMAIL_2, PASSWORD_CORRECT));
        assertEquals("Email is not registered", exception.getMessage());
        exception = assertThrows(EmptyEmailException.class,
                () -> personService.checkLoginAndPassword(USER_EMAIL, PASSWORD_INCORRECT));
        assertEquals("Incorrect password", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void updateUserInfo() {
        var userRq = new UserRq();
        userRq.setCity("Racoon City");
        userRq.setFirstName("Leon");
        userRq.setLastName("Kennedy");
        var retVal = personService.updateUserInfo("anything", userRq);
        assertNotNull(retVal);
        assertEquals(HttpStatus.OK, retVal.getStatusCode());
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons", PERSON_ROW_MAPPER);
        assertEquals(userRq.getFirstName(), person.getFirstName());
        assertEquals(userRq.getLastName(), person.getLastName());
        assertEquals(userRq.getCity(), person.getCity());
        jdbcTemplate.update("UPDATE persons SET is_blocked = true WHERE id = ?", person.getId());
        retVal = personService.updateUserInfo("anything", userRq);
        assertNotNull(retVal);
        assertEquals(HttpStatus.FORBIDDEN, retVal.getStatusCode());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    @Sql(statements = "INSERT INTO person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) VALUES (false, true, true, true, false, false, false)")
    void getPersonSettings() {
        var retVal = personService.getPersonSettings("anything");
        var data = retVal.getData();
        for (PersonSettingsRs personSettingsRs : data) {
            switch (personSettingsRs.getType()) {
                case "COMMENT_COMMENT":
                    assertFalse(personSettingsRs.getEnable());
                    break;
                case "FRIEND_BIRTHDAY":
                    assertTrue(personSettingsRs.getEnable());
                    break;
                case "FRIEND_REQUEST":
                    assertTrue(personSettingsRs.getEnable());
                    break;
                case "POST_LIKE":
                    assertTrue(personSettingsRs.getEnable());
                    break;
                case "MESSAGE":
                    assertFalse(personSettingsRs.getEnable());
                    break;
                case "POST_COMMENT":
                    assertFalse(personSettingsRs.getEnable());
                    break;
                case "POST":
                    assertFalse(personSettingsRs.getEnable());
            }
        }
    }

    @Test
    void setSetting() {
    }
}
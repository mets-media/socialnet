package socialnet.repository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.api.request.UserUpdateDto;
import socialnet.model.Person;
import socialnet.model.SearchOptions;
import socialnet.utils.Reflection;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class PersonRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Reflection reflection;

    public static final RowMapper<Person> PERSON_ROW_MAPPER = (resultSet, rowNum) -> {
        Person person = new Person();
        person.setId(resultSet.getLong("id"));
        person.setAbout(resultSet.getString("about"));
        person.setBirthDate(resultSet.getTimestamp("birth_date"));
        person.setChangePasswordToken(resultSet.getString("change_password_token"));
        person.setConfigurationCode(resultSet.getInt("configuration_code"));
        person.setDeletedTime(resultSet.getTimestamp("deleted_time"));
        person.setEmail(resultSet.getString("email"));
        person.setFirstName(resultSet.getString("first_name"));
        person.setIsApproved(resultSet.getBoolean("is_approved"));
        person.setIsBlocked(resultSet.getBoolean("is_blocked"));
        person.setIsDeleted(resultSet.getBoolean("is_deleted"));
        person.setLastName(resultSet.getString("last_name"));
        person.setLastOnlineTime(resultSet.getTimestamp("last_online_time"));
        person.setMessagePermissions(resultSet.getString("message_permissions"));
        person.setNotificationsSessionId(resultSet.getString("notifications_session_id"));
        person.setOnlineStatus(resultSet.getString("online_status"));
        person.setPassword(resultSet.getString("password"));
        person.setPhone(resultSet.getString("phone"));
        person.setPhoto(resultSet.getString("photo"));
        person.setRegDate(resultSet.getTimestamp("reg_date"));
        person.setCity(resultSet.getString("city"));
        person.setCountry(resultSet.getString("country"));
        person.setTelegramId(resultSet.getLong("telegram_id"));
        person.setPersonSettingsId(resultSet.getLong("person_settings_id"));

        return person;
    };

    private static final String AND = "' AND ";

    private static final String SELECT = "SELECT * FROM persons";

    private static final String SELECT2 = "SELECT DISTINCT p.id, p.about, p.birth_date," +
                                          " p.change_password_token, p.configuration_code, p.deleted_time," +
                                          " p.email, p.first_name, p.is_approved, p.is_blocked," +
                                          " p.is_deleted, p.last_name, p.last_online_time," +
                                          " p.message_permissions, p.notifications_session_id, p.online_status," +
                                          " p.password, p.phone, p.photo, p.reg_date, p.city," +
                                          " p.country, p.telegram_id, p.person_settings_id FROM persons AS p";

    private static final String AND_NOT_ID = " AND NOT id IN (";


    public Long insert(Person person) {
        String sql = "Insert into Persons " + reflection.getFieldNames(person, new String[]{"id"}) +
                     " values " + reflection.getStringValues(person, "id") + ") " +
                     " returning Id";
        Object[] values = reflection.getValues(person, "id");
        jdbcTemplate.update(sql, values);


        return null;
    }

    public void save(Person person) {
        jdbcTemplate.update(
                "INSERT INTO persons " +
                "(email, first_name, last_name, password, reg_date, is_approved, is_blocked, is_deleted, telegram_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                person.getEmail().toLowerCase(),
                person.getFirstName(),
                person.getLastName(),
                person.getPassword(),
                person.getRegDate(),
                person.getIsApproved(),
                person.getIsBlocked(),
                person.getIsDeleted(),
                person.getTelegramId()
        );
    }

    public List<Person> findPersonsByBirthDate() {
        return jdbcTemplate.query(SELECT + " as p  " +
                                  "where extract(month from timestamp 'now()')=extract(month from p.birth_date) " +
                                  "and extract(day from timestamp 'now()')=extract(day from p.birth_date)", PERSON_ROW_MAPPER);
    }

    public Person findByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT + " WHERE lower(email) = ?",
                    PERSON_ROW_MAPPER,
                    email.toLowerCase()
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Person findByTelegramId(long telegramId) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT + " WHERE telegram_id = ?",
                    PERSON_ROW_MAPPER,
                    telegramId
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Person findById(Long personId) {
        try {
            return jdbcTemplate.queryForObject(SELECT + " WHERE id = ?",
                    new BeanPropertyRowMapper<>(Person.class), personId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Person> findAll() {
        try {
            return jdbcTemplate.query(SELECT, PERSON_ROW_MAPPER);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public List<Person> findFriendsAll(Long id, Integer offset, Integer perPage) {
        try {
            return jdbcTemplate.query(SELECT2 + " JOIN friendships ON" +
                                      " friendships.dst_person_id=p.id OR friendships.src_person_id=p.id WHERE is_deleted = false" +
                                      " AND (friendships.dst_person_id=? OR friendships.src_person_id=?) AND" +
                                      " friendships.status_name='FRIEND' AND NOT p.id=? ORDER BY p.last_online_time DESC" +
                                      " OFFSET ? LIMIT ?",
                    PERSON_ROW_MAPPER, id, id, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public List<Person> findAllOutgoingRequests(Long id, Integer offset, Integer perPage) {
        try {
            return jdbcTemplate.query(SELECT2 + " JOIN friendships ON friendships.src_person_id=p.id" +
                                      "  OR friendships.dst_person_id=p.id" +
                                      " WHERE is_deleted = false AND friendships.src_person_id=? AND NOT p.id=?" +
                                      " AND friendships.status_name = 'REQUEST' ORDER BY p.last_online_time DESC OFFSET ? LIMIT ?",
                    PERSON_ROW_MAPPER, id, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Integer findAllOutgoingRequestsAll(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT DISTINCT COUNT(p.id) FROM persons AS p JOIN" +
                                               " friendships ON friendships.dst_person_id=p.id OR friendships.src_person_id=p.id" +
                                               " WHERE is_deleted = false AND status_name = 'REQUEST' AND src_person_id = ? AND NOT p.id=?",
                    Integer.class, id, id);
        } catch (EmptyResultDataAccessException ignored) {
            return 0;
        }
    }

    public Integer findFriendsAllCount(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT DISTINCT COUNT(persons.id) FROM persons JOIN" +
                                               " friendships ON friendships.dst_person_id=persons.id" +
                                               " OR friendships.src_person_id=persons.id WHERE is_deleted = false AND" +
                                               " (friendships.dst_person_id=? OR friendships.src_person_id=?)" +
                                               " AND friendships.status_name='FRIEND'  AND NOT persons.id=?",
                    Integer.class, id, id, id);
        } catch (EmptyResultDataAccessException ignored) {
            return 0;
        }
    }

    public List<Person> findByCity(String city) {
        try {
            return this.jdbcTemplate.query(
                    SELECT + " WHERE city = ?",
                    new Object[]{city},
                    PERSON_ROW_MAPPER
            );
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public void markUserDelete(String email) {
        jdbcTemplate.update("Update Persons Set is_deleted = true Where email = ?", email);
    }

    public void recover(String email) {
        jdbcTemplate.update("Update Persons Set is_deleted = false Where email = ?", email);
    }

    public void setPhoto(String photoHttpLink, Long userId) {
        jdbcTemplate.update("Update Persons Set photo = ? Where id = ?", photoHttpLink, userId);
    }

    public void setEmail(String oldEmail, String newEmail) {
        jdbcTemplate.update("Update Persons Set email = ? Where email = ?", newEmail, oldEmail);
    }

    public void setPassword(String newPassword, String email) {
        jdbcTemplate.update("Update Persons Set password = ? Where email = ?", newPassword, email);
    }

    public Long getPersonIdByEmail(String email) {
        return jdbcTemplate.queryForObject("Select id from Persons where email = ?",
                new Object[]{email}, Long.class);
    }

    public void updatePersonInfo(UserUpdateDto userData, String email) {
        var sqlParam = reflection.getFieldsAndValuesQuery(userData, new Object[]{email});
        jdbcTemplate.update("Update Persons Set " + sqlParam.get("fieldNames") + " where email = ?",
                (Object[]) sqlParam.get("values"));
    }

    public List<Person> findPersonsQuery(SearchOptions searchOptions) {

        try {
            return jdbcTemplate.query(createSqlPerson(searchOptions), PERSON_ROW_MAPPER, searchOptions.getOffset(),
                    searchOptions.getPerPage());
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Integer findPersonsQueryAll(SearchOptions searchOptions) {

        try {
            return jdbcTemplate.queryForObject(createSqlPerson(searchOptions), Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return 0;
        }
    }

    private String createSqlPerson(SearchOptions searchOptions) {

        StringBuilder str = new StringBuilder();
        String sql;
        if (Boolean.TRUE.equals(searchOptions.getFlagQueryAll())) {
            str.append("SELECT COUNT(*) FROM persons WHERE is_deleted=false AND ");
        } else {
            str.append(SELECT + " WHERE is_deleted=false AND ");
        }
        Timestamp ageFromTimestamp = searchDate(searchOptions.getAgeFrom());
        Timestamp ageToTimestamp = searchDate(searchOptions.getAgeTo());
        if (searchOptions.getFirstName().equals("'")) {
            searchOptions.setFirstName("\"");
        }
        str.append(searchOptions.getAgeFrom() > 0 ? " birth_date < '" + ageFromTimestamp + AND : "")
                .append(searchOptions.getAgeTo() > 0 ? " birth_date > '" + ageToTimestamp + AND : "")
                .append(!searchOptions.getCity().equals("") ? " city = '" + searchOptions.getCity() + AND : "")
                .append(!searchOptions.getCountry().equals("") ? " country = '" + searchOptions.getCountry() + AND : "")
                .append(!searchOptions.getFirstName().equals("") ? " lower (first_name) = '" + searchOptions.getFirstName()
                        .toLowerCase() + AND : "")
                .append(!searchOptions.getLastName().equals("") ? " lower (last_name) = '" + searchOptions.getLastName()
                        .toLowerCase() + AND : "")
                .append(" NOT id = ")
                .append(searchOptions.getId())
                .append(" ");

        if (str.substring(str.length() - 5).equals(" AND ")) {
            sql = str.substring(0, str.length() - 5);
        } else {
            sql = str.toString();
        }
        if (Boolean.FALSE.equals(searchOptions.getFlagQueryAll())) {
            return sql + " OFFSET ? LIMIT ?";
        } else {
            return sql;
        }
    }

    private Timestamp searchDate(Integer ageSearch) {
        val timestamp = new Timestamp(new Date().getTime());
        timestamp.setYear(timestamp.getYear() - ageSearch);
        return timestamp;
    }

    public List<Person> findPersonsName(String author) {
        try {
            return jdbcTemplate.query(SELECT +
                                      " WHERE is_deleted=false AND lower (first_name) = ? AND lower (last_name) = ?",
                    PERSON_ROW_MAPPER, author.substring(0, author.indexOf(" ")).toLowerCase(),
                    author.substring(author.indexOf(" ") + 1).toLowerCase());
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public List<Person> findPersonsFirstNameOrLastName(String name) {
        try {
            return jdbcTemplate.query(SELECT +
                                      " WHERE is_deleted=false AND lower (first_name) = ? OR lower (last_name) = ?",
                    PERSON_ROW_MAPPER, name.toLowerCase(), name.toLowerCase());
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }


    public void updateOnlineStatus(Long personId, String status) {
        jdbcTemplate.update("UPDATE persons SET online_status = ? WHERE id = ?", status, personId);
    }

    public void updateLastOnlineTime(Long personId, Timestamp lastOnlineTime) {
        jdbcTemplate.update("UPDATE persons SET last_online_time = ? WHERE id = ?", lastOnlineTime, personId);
    }

    public List<Person> findRecommendedFriends(Long id, List<Long> friends, Integer offset, Integer perPage,
                                               List<Long> notFriends) {
        StringBuilder sql = new StringBuilder(SELECT2 + " JOIN friendships ON friendships.dst_person_id=p.id" +
                                              " OR friendships.src_person_id=p.id WHERE is_deleted = false AND ")
                .append(createSqlWhere(id, friends, offset, perPage, notFriends));
        try {
            return jdbcTemplate.query(sql.toString(), PERSON_ROW_MAPPER);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public String createSqlWhere(Long id, List<Long> friends, Integer offset, Integer perPage, List<Long> notFriends) {
        StringBuilder strWhere = new StringBuilder();
        if (!friends.isEmpty()) {
            strWhere.append(" (friendships.dst_person_id IN (")
                    .append(StringUtils.join(friends, ", "))
                    .append(") OR friendships.src_person_id IN (")
                    .append(StringUtils.join(friends, ", "))
                    .append("))")
                    .append(" AND NOT p.id IN (")
                    .append(StringUtils.join(friends, ", "))
                    .append(") ");
            if (!notFriends.isEmpty()) {
                strWhere.append(" AND NOT p.id IN (")
                        .append(StringUtils.join(notFriends, ", "))
                        .append(")");
            }
                strWhere.append(" AND NOT p.id=")
                    .append(id);
        } else {
            strWhere.append(" NOT p.id=").append(id);
        }
        strWhere.append(" OFFSET ").append(offset).append(" LIMIT ").append(perPage);
        return strWhere.toString();
    }

    public List<Person> findAllPotentialFriends(Long id, Integer offset, Integer perPage) {
        try {
            return jdbcTemplate.query(SELECT2 + " JOIN friendships ON friendships.dst_person_id=p.id OR friendships.src_person_id=p.id" +
                                      " WHERE is_deleted = false AND friendships.dst_person_id=? AND NOT p.id=?" +
                                      " AND friendships.status_name IN ('REQUEST', 'RECEIVED_REQUEST') OFFSET ? LIMIT ?",
                    PERSON_ROW_MAPPER, id, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Long countAllPotentialFriends(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT DISTINCT COUNT(p.*) FROM persons AS p " +
                "  JOIN friendships ON friendships.dst_person_id=p.id OR friendships.src_person_id=p.id " +
                " WHERE is_deleted = false AND friendships.dst_person_id=? AND NOT p.id=? " +
                "   AND friendships.status_name  IN ('REQUEST', 'RECEIVED_REQUEST') ",
                Long.class, id, id);
    }

    public List<Person> findByCityForFriends(Long id, String city, List<String> recommendedFriendsIdList,
                                             Integer offset, Integer perPage, List<Long> notFriends) {
        StringBuilder sql = new StringBuilder(SELECT + " WHERE is_deleted = false AND city = ?")
                .append(!recommendedFriendsIdList.isEmpty() ? AND_NOT_ID + " " +
                                                              StringUtils.join(recommendedFriendsIdList, ",") + ")" : "")
                .append(!notFriends.isEmpty() ? AND_NOT_ID + " " + StringUtils.join(notFriends, ",") + ")" : "")
                .append(" AND NOT id=? ORDER BY reg_date DESC OFFSET ? LIMIT ?");
        try {
            return jdbcTemplate.query(sql.toString(), PERSON_ROW_MAPPER, city, id, offset, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public List<Person> findAllForFriends(Long id, String friendsRecommended, Integer perPage, List<Long> notFriends) {
        StringBuilder sql = new StringBuilder(SELECT + " WHERE is_deleted = false ")
                .append(!Objects.equals(friendsRecommended, "") ? AND_NOT_ID + friendsRecommended + ")" : "")
                .append(!notFriends.isEmpty() ? AND_NOT_ID + StringUtils.join(notFriends, ",") + ")" : "")
                .append(" AND NOT id=? ORDER BY reg_date DESC LIMIT ?");
        try {
            return jdbcTemplate.query(sql.toString(), PERSON_ROW_MAPPER, id, perPage);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Integer getAllUsersByCountry(String country) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM persons WHERE country=?", Integer.class, country);
    }

    public List<Person> findNotFriends(Long id) {
        try {
            return jdbcTemplate.query(SELECT2 + " JOIN friendships ON friendships.src_person_id=p.id" +
                                      " OR friendships.dst_person_id=p.id" +
                                      " WHERE is_deleted = false AND NOT p.id=? AND (friendships.src_person_id=?" +
                                      " OR friendships.dst_person_id=?) AND friendships.status_name = 'BLOCKED'",
                    PERSON_ROW_MAPPER, id, id, id);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }
}

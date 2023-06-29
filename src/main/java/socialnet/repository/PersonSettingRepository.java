package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.api.request.PersonSettingsRq;
import socialnet.model.PersonSettings;
import socialnet.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PersonSettingRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Reflection reflection;

    public void insert(String email) {
        String sql = "DO\n" +
                "$$" +
                "declare new_id bigint;\n" +
                "begin\n" +
                "  select id from persons where email = '%s' into new_id;\n" +
                "  insert into person_settings (id) values (new_id);\n" +
                "end$$;\n";
        sql = String.format(sql, email);
        jdbcTemplate.update(sql);
    }

    public PersonSettings getSettings(Long personId) {
        return jdbcTemplate.query("Select * from Person_Settings Where Id = ?",
                new BeanPropertyRowMapper<>(PersonSettings.class), personId).stream()
                .findAny().orElse(null);
    }

    public List<PersonSettings> getListSettings(Long personId) {
        PersonSettings personSettings = jdbcTemplate.query("Select * from Person_Settings Where Id = ?",
                new BeanPropertyRowMapper<>(PersonSettings.class), personId).stream().findAny().orElse(null);

        if (personSettings == null) return Collections.emptyList();

        List<PersonSettings> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : reflection.getFieldsAndValues(personSettings).entrySet()) {
            if (!entry.getKey().equalsIgnoreCase("id"))
                result.add((PersonSettings) entry.getValue());
        }
        return result;
    }
    public void setSetting(Long personId, PersonSettingsRq personSettingsRq) {
        String sql = String.format("Update Person_Settings Set %s = %s where id = ?",
                personSettingsRq.getNotificationType(),
                personSettingsRq.getEnable().toString());
        jdbcTemplate.update(sql, personId);
    }

    public void updatePersonSetting(Boolean enable, String typeNotification, Long id) {
        jdbcTemplate.update(
            "update person_settings set ? = ? where id = ?",
            typeNotification, enable, id
        );
    }
}

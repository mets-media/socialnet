package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import socialnet.model.Dialog;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DialogsRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Dialog> dialogRowMapper = (rs, rowNum) -> Dialog.builder()
            .id(rs.getLong("id"))
            .firstPersonId(rs.getLong("first_person_id"))
            .secondPersonId(rs.getLong("second_person_id"))
            .lastActiveTime(rs.getTimestamp("last_active_time"))
            .lastMessageId(rs.getLong("last_message_id"))
            .build();

    public List<Dialog> findByAuthorId(Long authorId) {
        return jdbcTemplate.query(
                "SELECT * FROM dialogs WHERE first_person_id = ?",
                dialogRowMapper,
                authorId);
    }


    public List<Dialog> findByRecipientId(Long recipientId) {
        return jdbcTemplate.query(
                "SELECT * FROM dialogs WHERE second_person_id = ?",
                dialogRowMapper,
                recipientId);
    }

    public Dialog findByAuthorAndRecipient(Long authorId, Long recipientId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM dialogs WHERE first_person_id = ? AND second_person_id = ?",
                    dialogRowMapper,
                    authorId, recipientId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Dialog findByAutorOrRecipient(long autorId, long recipientId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * " +
                "  FROM dialogs " +
                " WHERE (first_person_id = ? AND second_person_id = ?) " +
                "    OR (first_person_id = ? AND second_person_id = ?)",
                dialogRowMapper,
                autorId, recipientId,
                recipientId, autorId
            );
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Dialog findByDialogId(Long dialogId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM dialogs WHERE id = ?",
                    dialogRowMapper,
                    dialogId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Integer findDialogCount() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dialogs", Integer.class);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }


    public Integer findDialogsUserCount(Integer userId) throws EmptyResultDataAccessException {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dialogs WHERE first_person_id = ?" +
                                               " OR second_person_id = ?", Integer.class, userId, userId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public int update(Dialog dialog) {
        return jdbcTemplate.update("UPDATE dialogs SET last_active_time = ?, last_message_id = ? WHERE id = ?",
                dialog.getLastActiveTime(),
                dialog.getLastMessageId(),
                dialog.getId());
    }

    public Long save(Dialog dialog) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO dialogs (first_person_id, second_person_id, last_active_time, last_message_id) " +
                    "VALUES (?, ?, ?, ?)", new String[]{"id"});
            setValue(dialog.getFirstPersonId(), preparedStatement, 1);
            setValue(dialog.getSecondPersonId(), preparedStatement, 2);
            setValue(dialog.getLastActiveTime(), preparedStatement, 3);
            setValue(dialog.getLastMessageId(), preparedStatement, 4);

            return preparedStatement;
        }, keyHolder);

        return (long) keyHolder.getKey();
    }

    private <T> void setValue(T arg, PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        if (arg == null) {
            preparedStatement.setNull(parameterIndex, Types.NULL);
        } else {
            preparedStatement.setObject(parameterIndex, arg);
        }
    }

    public int updateField(long dialogId, String fieldName, long fieldValue) {
        return jdbcTemplate.update("UPDATE dialogs SET ? = ? WHERE id = ?",
            fieldName, fieldValue, dialogId);
    }
}

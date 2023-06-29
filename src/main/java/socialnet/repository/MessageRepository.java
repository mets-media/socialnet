package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import socialnet.model.Message;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Message> messageRowMapper = (rs, rowNum) -> Message.builder()
            .id(rs.getLong("id"))
            .isDeleted(rs.getBoolean("is_deleted"))
            .messageText(rs.getString("message_text"))
            .readStatus(rs.getString("read_status"))
            .time(rs.getTimestamp("time"))
            .dialogId(rs.getLong("dialog_id"))
            .authorId(rs.getLong("author_id"))
            .recipientId(rs.getLong("recipient_id"))
            .build();

    public Message findById(Long messageId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM messages WHERE id = ?",
                    messageRowMapper,
                    messageId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Message> findByDialogId(Long dialogId, Integer offset, Integer perPage) {
        return jdbcTemplate.query(
                "SELECT * FROM messages WHERE dialog_id = ? AND is_deleted = false ORDER BY id OFFSET ? LIMIT ?",
                messageRowMapper,
                dialogId, offset, perPage);
    }

    public Long countByDialogId(Long dialogId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM messages WHERE dialog_id = ? AND is_deleted = false",
                Long.class, dialogId);
    }

    public Long findCountByDialogIdAndReadStatus(Long dialogId, String readStatus) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages WHERE dialog_id = ? AND read_status = ?",
                Long.class,
                dialogId, readStatus);
    }

    public Message findByAuthorId(Long authorId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM messages WHERE author_id = ? ORDER BY time DESC LIMIT 1;",
                messageRowMapper,
                authorId);
    }

    public Long findCountByPersonIdAndReadStatus(Long personId, String readStatus) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages WHERE (author_id = ? OR recipient_id = ?) AND read_status = ?",
                Long.class,
                personId, personId, readStatus);
    }

    public Integer updateReadStatusByDialogId(Long dialogId, String readStatus, String queryStatus) {
        return jdbcTemplate.update("UPDATE messages SET read_status = ? WHERE dialog_id = ? AND read_status = ?",
                readStatus, dialogId, queryStatus);
    }

    public Long save(Message message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement prepStatement = connection.prepareStatement(
                    "INSERT INTO messages (is_deleted, " +
                    "message_text, " +
                    "read_status, " +
                    "time, " +
                    "dialog_id, " +
                    "author_id, " +
                    "recipient_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)", new String[]{"id"});
            setValue(message.getIsDeleted(), prepStatement, 1);
            setValue(message.getMessageText(), prepStatement, 2);
            setValue(message.getReadStatus(), prepStatement, 3);
            setValue(message.getTime(), prepStatement, 4);
            setValue(message.getDialogId(), prepStatement, 5);
            setValue(message.getAuthorId(), prepStatement, 6);
            setValue(message.getRecipientId(), prepStatement, 7);

            return prepStatement;
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

    public void markDeleted(Long messageId, Boolean isDeletedState) {
        jdbcTemplate.update("UPDATE messages SET is_deleted = ? WHERE id = ?", isDeletedState, messageId);
    }

    public Integer updateTextById(String text, Long messageId) {
        return jdbcTemplate.update("UPDATE messages SET message_text = ? WHERE id = ?", text, messageId);
    }

    public Integer getAllMessage() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages", Integer.class);
    }

    public List<Message> getMessage(Integer firstUserId, Integer secondUserId) {
        try {
            return jdbcTemplate.query("SELECT * FROM messages WHERE (author_id = ? AND recipient_id = ?)" +
                                      " OR (recipient_id = ? AND author_id = ?)",
                    messageRowMapper,
                    firstUserId, secondUserId, firstUserId, secondUserId);
        } catch (EmptyResultDataAccessException ignored) {
            return Collections.emptyList();
        }
    }

    public Integer getMessageByDialog(Integer dialogId) {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages WHERE dialog_id = ?",
                    Integer.class, dialogId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}

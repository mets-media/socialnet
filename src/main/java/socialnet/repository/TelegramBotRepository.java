package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.api.response.TgMessagesRs;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TelegramBotRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean register(long telegramId, String email, String cmd) {
        int cnt = jdbcTemplate.queryForObject(
            "select count(1) from persons where lower(email) = ? and telegram_id is not null",
            Integer.class,
            email.toLowerCase()
        );

        if (cnt > 0) {
            return false;
        }

        jdbcTemplate.update(
            "UPDATE persons SET telegram_id = ? WHERE lower(email) = ?",
            cmd.equals("register") ? telegramId : null,
            email.toLowerCase()
        );

        return true;
    }

    public boolean unregister(long telegramId) {
        try {
            jdbcTemplate.update(
                "UPDATE persons SET telegram_id = null WHERE telegram_id = ?",
                telegramId
            );
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    public Long getTelegramIdByPersonId(long personId) {
        return jdbcTemplate.queryForObject(
            "select telegram_id from persons where id = ?",
            Long.class,
            personId
        );
    }

    public List<TgMessagesRs> getMessages(long userId, String offsetPerPage) {
        String[] opp = offsetPerPage.split(";");
        int offset = Integer.parseInt(opp[0]);
        int perPage = Integer.parseInt(opp[1]);

        return jdbcTemplate.query(
            "SELECT d.id, " +
            "       m.message_text as msg, " +
            "       p.first_name || ' ' || p.last_name as from " +
            "  FROM messages m, " +
            "       dialogs d, " +
            "       persons p " +
            " WHERE m.recipient_id = d.second_person_id " +
            "   AND p.id = m.author_id " +
            "   AND m.read_status = 'SENT' " +
            "   AND m.recipient_id = ? " +
            " ORDER BY m.author_id " +
            "OFFSET ? LIMIT ?",
            (rs, rowNum) -> TgMessagesRs.builder()
                .dialogId(rs.getLong(1))
                .message(rs.getString(2))
                .from(rs.getString(3))
                .build(),
            userId, offset, perPage
        );
    }

    public long getCountMessages(long userId) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM messages WHERE recipient_id = ? AND read_status = 'UNREAD'",
            Long.class,
            userId
        );
    }
}

package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import socialnet.model.Friendships;
import socialnet.model.enums.FriendshipStatusTypes;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendsShipsRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_SELECT = "SELECT * FROM friendships";
    private static final String DST_SRC = " OR (dst_person_id = ? AND src_person_id = ?)";

    private final RowMapper<Friendships> friendshipsRowMapper = (resultSet, rowNum) -> {
        Friendships friendships = new Friendships();
        friendships.setId(resultSet.getLong("id"));
        friendships.setSentTime(resultSet.getTimestamp("sent_time"));
        friendships.setDstPersonId(resultSet.getLong("dst_person_id"));
        friendships.setSrcPersonId(resultSet.getLong("src_person_id"));
        friendships.setStatusName(FriendshipStatusTypes.valueOf(resultSet.getString("status_name")));
        return friendships;
    };

    public Friendships findFriend(Long id, Long idFriend) {
        try {
            return jdbcTemplate.queryForObject(SQL_SELECT +
                            " WHERE sent_time IS NOT NULL " +
                            " AND ((src_person_id = ? AND dst_person_id = ?)" +
                            DST_SRC + ")",
                    friendshipsRowMapper, id, idFriend, idFriend, id);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void addFriend(Long id, Long idFriend, FriendshipStatusTypes status) {
        jdbcTemplate.update("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name)" +
                " VALUES (NOW(), ?, ?, ?)", idFriend, id,  status.toString());
    }

    public void updateFriend(Long id, Long idFriend, FriendshipStatusTypes status, Long idRequest) {
        jdbcTemplate.update("UPDATE friendships SET sent_time=NOW()," +
                        " dst_person_id=?, src_person_id=?, status_name=? WHERE id=?", idFriend, id, status.toString(),
                idRequest);
    }

    public void deleteFriendUsing(Long personsId, Long idFriend) {
        jdbcTemplate.update("DELETE FROM friendships WHERE ((dst_person_id = ? AND src_person_id = ?) "
                        + DST_SRC + ")", personsId, idFriend, idFriend, personsId);
    }

    public Friendships findRequest(Long id, Long idFriend) {
        try {
            return jdbcTemplate.queryForObject(SQL_SELECT +
                            " WHERE status_name = 'REQUEST' AND (dst_person_id = ? AND src_person_id = ?)" +
                            DST_SRC,
                    friendshipsRowMapper, id, idFriend, idFriend, id);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Friendships getFriendStatus(Long id, Long idFriend) {
        try {
            return jdbcTemplate.queryForObject(SQL_SELECT +
                    " WHERE sent_time IS NOT NULL" +
                    " AND (dst_person_id = ? AND src_person_id = ?)" +
                    DST_SRC + " AND NOT status_name = 'BLOCKED'", friendshipsRowMapper, id, idFriend, idFriend, id);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public List<Friendships> findAllFriendships(Long id) {
        return jdbcTemplate.query("select * from friendships as f where f.status_name = 'FRIEND' " +
                "and (f.dst_person_id =? OR f.src_person_id =?)", friendshipsRowMapper, id, id);
    }

    public Friendships getFriendStatusBlocked(long personId, long idFriend) {
        try {
            return jdbcTemplate.queryForObject(SQL_SELECT +
                    " WHERE sent_time IS NULL AND status_name = 'BLOCKED' AND src_person_id = ? AND dst_person_id = ?",
                    friendshipsRowMapper, personId, idFriend);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public Friendships getFriendStatusBlocked2(long personId, long idFriend) {
        try {
            return jdbcTemplate.queryForObject(SQL_SELECT +
                            " WHERE sent_time IS NULL AND status_name = 'BLOCKED' AND " +
                            " (src_person_id = ? AND dst_person_id = ?)",
                    friendshipsRowMapper, personId, idFriend);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    public void deleteStatusBlocked(Long id) {
        jdbcTemplate.update("DELETE FROM friendships WHERE sent_time IS NULL AND id = ?", id);
    }

    public void addStatusBlocked(Long id, Long idFriend) {
        jdbcTemplate.update("INSERT INTO friendships (sent_time, dst_person_id, src_person_id, status_name)" +
                " VALUES (null, ?, ?, 'BLOCKED')", idFriend, id);
    }
}

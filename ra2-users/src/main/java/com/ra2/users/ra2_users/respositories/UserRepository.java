package com.ra2.users.ra2_users.respositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ra2.users.ra2_users.models.User;

@Repository
public class UserRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setNom(rs.getString("nom"));
            u.setDescription(rs.getString("description"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setUltimAcces(rs.getTimestamp("ultimAcces"));
            u.setDataCreated(rs.getTimestamp("dataCreated"));
            u.setDataUpdated(rs.getTimestamp("dataUpdated"));
            return u;
        }
    };

    public int save(User usuari) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String sql = "INSERT INTO users (nom, description, email, password, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                usuari.getNom(),
                usuari.getDescription(),
                usuari.getEmail(),
                usuari.getPassword(),
                now,
                now);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userMapper);
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> result = jdbcTemplate.query(sql, userMapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public int update(Long id, User usuari) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE users SET nom = ?, description = ?, email = ?, password = ?, dataUpdated = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                usuari.getNom(),
                usuari.getDescription(),
                usuari.getEmail(),
                usuari.getPassword(),
                now,
                id);
    }

    public int updateName(Long id, String nom) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE users SET nom = ?, dataUpdated = ? WHERE id = ?";
        return jdbcTemplate.update(sql, nom, now, id);
    }

    public int delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}

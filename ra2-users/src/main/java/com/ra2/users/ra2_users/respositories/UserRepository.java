package com.ra2.users.ra2_users.respositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

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
            u.setImage_path(rs.getString("imatge_path"));
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

    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> result = jdbcTemplate.query(sql, userMapper, id);
        return result.get(0);
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
        String sql = "UPDATE users SET nom = ?, dataUpdated = ? WHERE id = ?";
        User user = findById(id);
        if (user == null){
            return jdbcTemplate.update(sql, id);
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql, nom, now, id);
    }

    public int updateImage(Long id, String imatge_path){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE users SET imatge_path = ?, dataUpdated = ? WHERE id = ?";
        return jdbcTemplate.update(sql, imatge_path, now, id);     
    }

    public int delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        User user = findById(id);
        if (user == null){
            return jdbcTemplate.update(sql, id);
        }
        return jdbcTemplate.update(sql, id);
    }
    
}

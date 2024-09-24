package DBController;

import model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDbSql {

    private JdbcTemplate jdbcTemplate;

    public UserDbSql(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Opretter en bruger i databasen
    public void createUser(User user) {
        try {
            String sql = "INSERT INTO login (username, password) VALUES (?, ?)";
            jdbcTemplate.update(sql, user.getUsername(), user.getPassword());
        } catch (RuntimeException e) {
            throw new RuntimeException("Fejl under oprettelse af bruger", e);
        }
    }

    // Sletter en bruger fra databasen
    public void deleteUser(Long id) {
        try {
            String sql = "DELETE FROM login WHERE id = ?";
            jdbcTemplate.update(sql, id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Fejl under sletning af bruger", e);
        }
    }

    // Opdaterer en bruger i databasen
    public void updateUser(User user) {
        try {
            String sql = "UPDATE login SET username = ?, password = ? WHERE id = ?";
            jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getUserid());
        } catch (RuntimeException e) {
            throw new RuntimeException("Fejl under opdatering af bruger", e);
        }
    }

    // Finder en bruger i databasen ud fra brugerens ID
    public Optional<User> findUserByID(Long userId) {
        // Vi bruger optional fordi vi kan returnere 0 uden at programmet går ned
        try {
            String sql = "SELECT * FROM login WHERE id = ?";
            User user = jdbcTemplate.queryForObject(sql, new Object[]{userId}, userRowMapper());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // Return empty Optional if no user is found
        } catch (DataAccessException e) {
            throw new RuntimeException("Brugeren findes ikke", e);
        }
    }

    // Finder en bruger i databasen baseret på username og adgangskode til login
    public User findLogin(String username, String password) {
        try {
            String sql = "SELECT * FROM login WHERE username = ? AND password = ?";
            List<User> users = jdbcTemplate.query(sql, new String[]{username, password}, userRowMapper());
            if (!users.isEmpty()) {
                return users.get(0);
            } else {
                return null;
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Mapper rækker fra databasen til User-objekter
    public RowMapper<User> userRowMapper() {
        // En anononym funktion som tager 2 parametre ResultSet og int
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserid(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            return user;
        };
    }
}
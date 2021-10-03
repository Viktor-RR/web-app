package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.*;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepository {
  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<User> rowMapper = resultSet -> new User(
          resultSet.getLong("id"),
          resultSet.getString("username")
  );
  private final RowMapper<UserWithPassword> rowMapperWithPassword = resultSet -> new UserWithPassword(
          resultSet.getLong("id"),
          resultSet.getString("username"),
          resultSet.getString("password")
  );
  private final RowMapper<String> rowMapperWithRole = resultSet ->
          resultSet.getString("role");

  private final RowMapper<UserWithToken> rowMapperWithToken = resultSet -> new UserWithToken(
          resultSet.getString("token"),
          resultSet.getLong("userId")
  );
  private final RowMapper<Instant> rowMapperTokenTime = resultSet ->
          resultSet.getTimestamp("created").toInstant();



  public Optional<User> getByUsername(String username) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne("SELECT id, username FROM users WHERE username = ?", rowMapper, username);
  }

  public Optional<UserWithPassword> getByUsernameWithPassword(String username) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne("SELECT id, username, password FROM users WHERE username = ?", rowMapperWithPassword, username);
  }

  public Optional<User> save(long id, String username, String hash) {
    // language=PostgreSQL
    return id == 0 ? jdbcTemplate.queryOne(
            """
                INSERT INTO users(username, password) VALUES (?, ?) RETURNING id, username;
                """,
            rowMapper,
            username,
            hash
    ) : jdbcTemplate.queryOne(
            """
                UPDATE users SET username = ?, password = ? WHERE id = ? RETURNING id, username
                """,
            rowMapper,
            username, hash, id
    );
  }

  public Optional<User> findByToken(String token) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne(
            """
                SELECT u.id, u.username FROM tokens t
                JOIN users u ON t."userId" = u.id
                WHERE t.token = ?
                """,
            rowMapper,
            token
    );
  }

  public void saveToken(long userId, String token) {
    // query - SELECT'ов (ResultSet)
    // update - ? int/long
    // language=PostgreSQL
    jdbcTemplate.update(
            """
            INSERT INTO tokens(token, "userId") VALUES (?, ?)
            """,
            token, userId
    );
  }

  public void saveRole(long id, String role) {
    // language=PostgreSQL
    jdbcTemplate.update("""
         INSERT INTO roles("userId", role) VALUES (?, ?)
         """,
            id,
            role
    );
  }

  public Optional<String> findByRole(long id) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne("""
            SELECT role FROM roles WHERE "userId" = ?
            """,
            rowMapperWithRole,
            id );
  }


  public Optional<UserWithToken> findToken(long id) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne("""
        SELECT token, "userId" FROM tokens WHERE "userId" = ?
""", rowMapperWithToken, id);
  }

  public Optional<Instant> findTokenDate(long id) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne("""
        SELECT created FROM tokens WHERE "userId" = ?
                """,
            rowMapperTokenTime, id);
  }

  public void updateToken(long id, String token, Timestamp newDate) {
    // language=PostgreSQL
    jdbcTemplate.update("""
        UPDATE tokens SET token = ?, created = ? WHERE "userId" = ?
""", token, newDate, id);
  }
}


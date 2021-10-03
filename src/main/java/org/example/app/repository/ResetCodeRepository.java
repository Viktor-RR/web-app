package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.Optional;

@RequiredArgsConstructor
public class ResetCodeRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Long> rowMapperCode = resultSet ->
            resultSet.getLong("code");

    public void saveCode(long id, int code) {
        // language=PostgreSQL
        jdbcTemplate.update("""
            INSERT INTO codes(id, code) VALUES (?, ?)
            """,
                id,
                code);
    }

    public Optional<Long> findCode(long id) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("""
            SELECT code FROM codes WHERE id = ?
            """,
                rowMapperCode,
                id);
    }

    public void deleteCode(long id) {
        // language=PostgreSQL
        jdbcTemplate.update("""
            DELETE FROM codes WHERE id = ?
            """,
                id);
    }
}

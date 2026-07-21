package com.analyticore.analysis.repository;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepository {

    private final JdbcTemplate jdbcTemplate;

    public JobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> findTextById(String jobId) {
        return jdbcTemplate.query(
                "SELECT text FROM jobs WHERE id = ?",
                resultSet -> {
                    if (resultSet.next()) {
                        return Optional.of(resultSet.getString("text"));
                    }

                    return Optional.empty();
                },
                jobId
        );
    }

    public void markAsProcessing(String jobId) {
        jdbcTemplate.update(
                """
                UPDATE jobs
                SET status = 'PROCESANDO',
                    updated_at = NOW()
                WHERE id = ?
                """,
                jobId
        );
    }

    public void complete(
            String jobId,
            String sentiment,
            String keywordsJson
    ) {
        jdbcTemplate.update(
                """
                UPDATE jobs
                SET status = 'COMPLETADO',
                    sentiment = ?,
                    keywords = CAST(? AS JSON),
                    updated_at = NOW()
                WHERE id = ?
                """,
                sentiment,
                keywordsJson,
                jobId
        );
    }

    public void markAsError(String jobId) {
        jdbcTemplate.update(
                """
                UPDATE jobs
                SET status = 'ERROR',
                    updated_at = NOW()
                WHERE id = ?
                """,
                jobId
        );
    }
}
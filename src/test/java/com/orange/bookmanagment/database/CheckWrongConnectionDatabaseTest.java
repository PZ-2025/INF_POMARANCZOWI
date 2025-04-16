package com.orange.bookmanagment.database;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
@SpringBootTest
@ActiveProfiles("test")
public class CheckWrongConnectionDatabaseTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassName;

    @Test
    public void testDatabaseConnection() throws SQLException {
        assertThat(dataSource).isNotNull();

        assertThat(jdbcTemplate).isNotNull();

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNull();
        }
    }

    @Test
    public void testExecuteQuery() {
        assertDoesNotThrow(() -> {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            assertThat(result).isNull();
        });
    }

    @Test
    public void testTableCreation() {
        assertDoesNotThrow(() -> {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_connection (id SERIAL PRIMARY KEY, name VARCHAR(100))");

            jdbcTemplate.execute("INSERT INTO test_connection (name) VALUES ('test_value')");

            String result = jdbcTemplate.queryForObject(
                    "SELECT name FROM test_connection WHERE name = 'test_value'", String.class);

            assertThat(result).isNull();
        });
    }
}
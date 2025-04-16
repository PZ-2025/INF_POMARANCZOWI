package com.orange.bookmanagment.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvalidCredentialsTest {

    private static final String VALID_URL = "jdbc:mysql://localhost:3306/librarydb";
    private static final String VALID_USERNAME = "root";
    private static final String VALID_PASSWORD = "root";
    private static final String INVALID_USERNAME = "invalid_user";
    private static final String INVALID_PASSWORD = "invalid_password";
    private static final String INVALID_URL = "jdbc:mysql://localhost:3306/non_existent_db";

    @BeforeAll
    public static void setup() {
        // Bezpośrednia próba załadowania sterownika MySQL
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Sterownik MySQL został pomyślnie załadowany");
        } catch (ClassNotFoundException e) {
            System.err.println("Błąd: Nie można załadować sterownika MySQL: " + e.getMessage());
            // Test prawdopodobnie się nie powiedzie, ale pozwólmy na to
        }
    }

    @Test
    public void testInvalidUsername() {
        // Test z niepoprawną nazwą użytkownika
        DataSource dataSource = new DriverManagerDataSource(
                VALID_URL,
                INVALID_USERNAME,
                VALID_PASSWORD
        );

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Próba wykonania zapytania powinna rzucić wyjątek
        assertThatThrownBy(() -> jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    public void testInvalidPassword() {
        // Test z niepoprawnym hasłem
        DataSource dataSource = new DriverManagerDataSource(
                VALID_URL,
                VALID_USERNAME,
                INVALID_PASSWORD
        );

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Próba wykonania zapytania powinna rzucić wyjątek
        assertThatThrownBy(() -> jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    public void testInvalidDatabaseUrl() {
        // Test z niepoprawnym URL do bazy danych
        DataSource dataSource = new DriverManagerDataSource(
                INVALID_URL,
                VALID_USERNAME,
                VALID_PASSWORD
        );

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Próba wykonania zapytania powinna rzucić wyjątek
        assertThatThrownBy(() -> jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Unknown database");
    }

    @Test
    public void testConnectionUsingDriverManager() {
        // Test z wykorzystaniem DriverManager.getConnection() - bardziej niskopoziomowe podejście
        assertThrows(SQLException.class, () -> {
            try (Connection connection = DriverManager.getConnection(
                    VALID_URL, INVALID_USERNAME, INVALID_PASSWORD)) {
                // To połączenie powinno się nie udać
            }
        });
    }

    @Test
    public void testValidConnection() {
        // Pomiń ten test, jeśli sterownik nie jest dostępny
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Pomijam test prawidłowego połączenia - sterownik nie jest dostępny");
            return;
        }

        // Test z poprawnymi danymi - dla porównania
        DataSource dataSource = new DriverManagerDataSource(
                VALID_URL,
                VALID_USERNAME,
                VALID_PASSWORD
        );

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Powinno działać bez wyjątku
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);
    }
}
package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var createTestTable = """            
                    CREATE TABLE if NOT EXISTS auth (
                                    username VARCHAR(255) NOT NULL,
                                    authToken VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (authToken)
                                    )""";
            try (var createTableStatement = conn.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public void addAuth(AuthData authData) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auth (username, authToken) VALUES(?, ?)")) {
                statement.setString(1, authData.username());
                statement.setString(2, authData.authToken());
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public void delAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection() ) {
            try (var statement = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                statement.setString(1, authToken);
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE authToken=?")) {
                statement.setString(1, authToken);
                try (var results = statement.executeQuery()) {
                    results.next();
                    var username = results.getString("username");
                    return new AuthData(username, authToken);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Auth Token does not exist: " + authToken);
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE auth")) {
                statement.executeUpdate();
            } catch (SQLException error) {
                throw new RuntimeException(error);
            }
        } catch (SQLException | DataAccessException error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public void addAuth(String authToken, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auth (username, authToken) VALUES(?, ?)")) {
                statement.setString(1, username);
                statement.setString(2, authToken);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Auth token already exists");
            }
            throw new DataAccessException("Database connection failed: " + e.getMessage());
        }
    }
}

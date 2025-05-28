package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var conn = DatabaseManager.getConnection()) {
            var createTestTable = """            
                    CREATE TABLE if NOT EXISTS user (
                                    username VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(255),
                                    PRIMARY KEY (username)
                                    )""";
            try (var createTableStatement = conn.prepareStatement(createTestTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException error) {
            throw new RuntimeException(error);
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean passwordMatches(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                statement.setString(1, username);
                try (var results = statement.executeQuery()) {
                    results.next();
                    var password = results.getString("password");
                    var email = results.getString("email");
                    return new UserData(username, password, email);
                }
            }
        } catch (SQLException error) {
            throw new DataAccessException("User not found: " + username);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                statement.setString(1, user.username());
                statement.setString(2, hashPassword(user.password()));
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
        } catch (SQLException error) {
            throw new DataAccessException("User already exists: " + user.username());
        }
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        return passwordMatches(password, user.password());
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE user")) {
                statement.executeUpdate();
            } catch (SQLException error) {
                throw new RuntimeException(error);
            }
        } catch (SQLException | DataAccessException error) {
            throw new RuntimeException(error);
        }
    }
    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                statement.setString(1, username);
                statement.setString(2, hashedPassword);
                statement.setString(3, email);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Username already taken");
            }
            throw new DataAccessException("failed to get connection: " + e.getMessage());
        }
    }
}

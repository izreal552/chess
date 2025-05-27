
package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setup() {
        userDAO = new MemoryUserDAO();  // Assuming in-memory test DAO
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void createUserPositive() {
        UserData user = new UserData("testuser", "pass123", "email@test.com");
        AuthData auth = assertDoesNotThrow(() -> userService.createUser(user));
        assertEquals("testuser", auth.username());
    }

    @Test
    void createUserNegative() throws BadRequestException, DataAccessException {
        UserData user = new UserData("dupe", "pass", null);
        userService.createUser(user);
        assertThrows(BadRequestException.class, () -> userService.createUser(user));
    }

    @Test
    void loginUserPositive() throws BadRequestException, DataAccessException {
        UserData user = new UserData("user", "pass", "mail");
        userService.createUser(user);
        AuthData auth = assertDoesNotThrow(() -> userService.loginUser(user));
        assertEquals("user", auth.username());
    }

    @Test
    void loginUserNegative() {
        UserData badUser = new UserData("wrong", null, "mail");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(badUser));
    }

    @Test
    void logoutUserPositive() throws BadRequestException, DataAccessException {
        UserData user = new UserData("logout", "1234", "mail");
        AuthData auth = userService.createUser(user);
        assertDoesNotThrow(() -> userService.logoutUser(auth.authToken()));
    }

    @Test
    void logoutUserNegative() {
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser("fake-token"));
    }

    @Test
    void clearPositive() {
        assertDoesNotThrow(() -> userService.clear());
    }
}

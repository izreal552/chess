
package passoff.server;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;

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
    void createUser_Positive() {
        UserData user = new UserData("testuser", "pass123", "email@test.com");
        AuthData auth = assertDoesNotThrow(() -> userService.createUser(user));
        assertEquals("testuser", auth.username());
    }

    @Test
    void createUser_Negative() throws BadRequestException {
        UserData user = new UserData("dupe", "pass", null);
        userService.createUser(user);
        assertThrows(BadRequestException.class, () -> userService.createUser(user));
    }

    @Test
    void loginUser_Positive() throws BadRequestException {
        UserData user = new UserData("user", "pass", "mail");
        userService.createUser(user);
        AuthData auth = assertDoesNotThrow(() -> userService.loginUser(user));
        assertEquals("user", auth.username());
    }

    @Test
    void loginUser_Negative() {
        UserData badUser = new UserData("wrong", null, "mail");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(badUser));
    }

    @Test
    void logoutUser_Positive() throws BadRequestException {
        UserData user = new UserData("logout", "1234", "mail");
        AuthData auth = userService.createUser(user);
        assertDoesNotThrow(() -> userService.logoutUser(auth.authToken()));
    }

    @Test
    void logoutUser_Negative() {
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser("fake-token"));
    }

    @Test
    void clear_Positive() {
        assertDoesNotThrow(() -> userService.clear());
    }
}

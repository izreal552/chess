package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private SQLUserDAO userDAO;
    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();

        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void CreateUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "pass", "email");
        AuthData auth = userService.createUser(user);
        assertNotNull(auth.authToken());
    }

    @Test
    void CreateUserNegative() {
        UserData user = new UserData("user1", "pass", "email");
        assertDoesNotThrow(() -> userService.createUser(user));
        assertThrows(BadRequestException.class, () -> userService.createUser(user));
    }

    @Test
    void LoginUserPositive() throws DataAccessException {
        UserData user = new UserData("user2", "pass", "email");
        userService.createUser(user);
        AuthData auth = userService.loginUser(user);
        assertNotNull(auth);
    }

    @Test
    void LoginUserNegative() {
        UserData user = new UserData("user3", "wrong", "email");
        assertThrows(DataAccessException.class, () -> userService.loginUser(user));
    }

    @Test
    void LogoutUserPositive() throws DataAccessException {
        UserData user = new UserData("user4", "pass", "email");
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

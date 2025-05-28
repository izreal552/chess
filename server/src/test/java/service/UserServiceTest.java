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
    public void setUp() {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();

        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
    }

    @Test
    public void testCreateUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "pass", "email");
        AuthData auth = userService.createUser(user);
        assertNotNull(auth.authToken());
    }

    @Test
    public void testCreateUserNegative() {
        UserData user = new UserData("user1", "pass", "email");
        assertDoesNotThrow(() -> userService.createUser(user));
        assertThrows(BadRequestException.class, () -> userService.createUser(user));
    }

    @Test
    public void testLoginUserPositive() throws DataAccessException {
        UserData user = new UserData("user2", "pass", "email");
        userService.createUser(user);
        AuthData auth = userService.loginUser(user);
        assertNotNull(auth);
    }

    @Test
    public void testLoginUserNegative() {
        UserData user = new UserData("user3", "wrong", "email");
        assertThrows(DataAccessException.class, () -> userService.loginUser(user));
    }

    @Test
    public void testLogoutUserPositive() throws DataAccessException {
        UserData user = new UserData("user4", "pass", "email");
        AuthData auth = userService.createUser(user);
        assertDoesNotThrow(() -> userService.logoutUser(auth.authToken()));
    }

    @Test
    public void testClearPositive(){
        userDAO.clear();
        authDAO.clear();
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser("some_token"));
    }
}

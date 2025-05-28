package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private UserService userService;
    private SQLUserDAO userDAO;
    private SQLGameDAO gameDAO;
    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp(){
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        authDAO = new SQLAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {
        UserData user = new UserData("gamer1", "pass", "email");
        AuthData auth = userService.createUser(user);
        HashSet<GameData> games = gameService.listGames(auth.authToken());
        assertNotNull(games);
    }

    @Test
    public void testListGamesNegative() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("bad_token"));
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        UserData user = new UserData("gamer2", "pass", "email");
        AuthData auth = userService.createUser(user);
        int gameId = gameService.createGame(auth.authToken(), "chess");
        assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGameNegative() {
        assertThrows(DataAccessException.class, () -> gameService.createGame("invalid", "chess"));
    }

    @Test
    public void testGetGamePositive() throws DataAccessException {
        UserData user = new UserData("gamer3", "pass", "email");
        AuthData auth = userService.createUser(user);
        int gameId = gameService.createGame(auth.authToken(), "checkers");
        GameData game = gameService.getGame(auth.authToken(), gameId);
        assertEquals("checkers", game.gameName());
    }

    @Test
    public void testGetGameNegative() throws DataAccessException {
        UserData user = new UserData("gamer4", "pass", "email");
        AuthData auth = userService.createUser(user);
        assertThrows(BadRequestException.class, () -> gameService.getGame(auth.authToken(), 9999));
    }

    @Test
    public void testClearPositive() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty());
    }
}

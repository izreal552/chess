
package passoff.server;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

public class GameServiceTest {
    private GameService gameService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private String token;

    @BeforeEach
    void setup() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("user", "token123");
        authDAO.addAuth(auth);
        token = auth.authToken();
    }

    @Test
    void listGames_Positive() {
        HashSet<GameData> games = assertDoesNotThrow(() -> gameService.listGames(token));
        assertNotNull(games);
    }

    @Test
    void listGames_Negative() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("bad-token"));
    }

    @Test
    void getGame_Positive() throws UnauthorizedException, BadRequestException {
        int created = gameService.createGame(token, "Test Game");
        GameData result = assertDoesNotThrow(() -> gameService.getGame(token, created));
        assertEquals("Test Game", result.gameName());
    }

    @Test
    void getGame_Negative() {
        assertThrows(UnauthorizedException.class, () -> gameService.getGame("bad-token", 1));
    }

    @Test
    void createGame_Positive() throws Exception {
        String gameName = "Epic Match";
        int gameID = gameService.createGame(token, gameName);
        assertTrue(gameID > 0);

        GameData createdGame = gameService.getGame(token, gameID);
        assertNotNull(createdGame);
        assertEquals(gameName, createdGame.gameName());
        assertNull(createdGame.whiteUsername());
        assertNull(createdGame.blackUsername());
        assertNotNull(createdGame.game());
    }

    @Test
    void createGame_Negative() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("bad-token", "No Game"));
    }

    @Test
    void joinGame_Positive() throws UnauthorizedException, BadRequestException {
        int game = gameService.createGame(token, "Join Game");
        assertDoesNotThrow(() -> gameService.joinGame(token,game, "WHITE"));
    }

    @Test
    void joinGame_Negative() {
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame("bad-token",99, "BLACK"));
    }
}

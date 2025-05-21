
package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

public class GameServiceTest {
    private GameService gameService;
    private String token;

    @BeforeEach
    void setup() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("user", "token123");
        authDAO.addAuth(auth);
        token = auth.authToken();
    }

    @Test
    void listGamesPositive() {
        HashSet<GameData> games = assertDoesNotThrow(() -> gameService.listGames(token));
        assertNotNull(games);
    }

    @Test
    void listGamesNegative() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("bad-token"));
    }

    @Test
    void getGamePositive() throws UnauthorizedException, BadRequestException {
        int created = gameService.createGame(token, "Test Game");
        GameData result = assertDoesNotThrow(() -> gameService.getGame(token, created));
        assertEquals("Test Game", result.gameName());
    }

    @Test
    void getGameNegative() {
        assertThrows(UnauthorizedException.class, () -> gameService.getGame("bad-token", 1));
    }

    @Test
    void createGamePositive() throws Exception {
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
    void createGameNegative() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("bad-token", "No Game"));
    }

    @Test
    void joinGamePositive() throws UnauthorizedException, BadRequestException {
        int game = gameService.createGame(token, "Join Game");
        assertDoesNotThrow(() -> gameService.joinGame(token,game, "WHITE"));
    }

    @Test
    void joinGameNegative() {
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame("bad-token",99, "BLACK"));
    }
    @Test
    void clearPositive() throws Exception {
        int gameID = gameService.createGame(token, "Clear Test Game");
        assertNotNull(gameService.getGame(token, gameID));
        gameService.clear();
        assertThrows(UnauthorizedException.class, () -> gameService.getGame(token, gameID));
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(token));
    }

}

package server;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.HashSet;

public class GameHandler {
    GameService gameService;
    public GameHandler(GameService gameService){
        this.gameService = gameService;
    }

    public Object listGames(Request request, Response response) throws DataAccessException{
        try {
            String authToken = request.headers("authorization");
            HashSet<GameData> games = gameService.listGames(authToken);
            response.status(200);
            return "{ \"games\": %s}".formatted(new Gson().toJson(games));
        } catch (UnauthorizedException e) {
            if (e.getMessage().toLowerCase().contains("unauthorized") || e.getMessage().toLowerCase().contains("invalid")) {
                throw new UnauthorizedException("unauthorized");
            } else {
                response.status(500);
                return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
            }
        } catch (Exception e) {
            response.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    public Object createGame(Request request, Response response) throws DataAccessException{
        try {
            String authToken = request.headers("authorization");
            if (authToken == null || authToken.trim().isEmpty()) {
                throw new UnauthorizedException("Missing or empty authorization token");
            }

            GameData gameData = new Gson().fromJson(request.body(), GameData.class);
            if (gameData == null || gameData.gameName() == null || gameData.gameName().isBlank()) {
                throw new BadRequestException("Missing or invalid 'gameName'");
            }

            int gameID = gameService.createGame(authToken, gameData.gameName());
            response.status(200);
            return "{ \"gameID\": %d }".formatted(gameID);

        } catch (BadRequestException e) {
            response.status(400);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";

        } catch (UnauthorizedException e) {
            response.status(401);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";

        } catch (DataAccessException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("already taken")) {
                response.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            } else {
                response.status(500);
                return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
            }

        } catch (Exception e) {
            response.status(500);
            return "{ \"message\": \"Error: Internal server error\" }";
        }
    }

    public Object joinGame(Request req, Response resp){
        try {
            if (!req.body().contains("\"gameID\":")) {
                resp.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            String authToken = req.headers("authorization");

            if (authToken == null || authToken.trim().isEmpty()) {
                resp.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            try {
                record JoinGameData(String playerColor, int gameID) {}
                JoinGameData joinData = new Gson().fromJson(req.body(), JoinGameData.class);

                boolean joinSuccess = gameService.joinGame(authToken, joinData.gameID(), joinData.playerColor());

                if (!joinSuccess) {
                    resp.status(403);
                    return "{ \"message\": \"Error: already taken\" }";
                }

                resp.status(200);
                return "{}";

            } catch (BadRequestException e) {
                resp.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            } catch (UnauthorizedException e) {
                String msg = e.getMessage();
                if (msg != null && (
                        msg.toLowerCase().contains("unauthorized") ||
                                msg.toLowerCase().contains("invalid token") ||
                                msg.toLowerCase().contains("auth")
                )) {
                    resp.status(401);
                    return "{ \"message\": \"Error: unauthorized\" }";
                } else {
                    // Unexpected UnauthorizedException = 500
                    resp.status(500);
                    return "{ \"message\": \"Error: " + (msg != null ? msg : "internal server error") + "\" }";
                }
            }

        } catch (Exception e) {
            resp.status(500);
            return "{ \"message\": \"Error: " + (e.getMessage() != null ? e.getMessage() : "internal server error") + "\" }";
        }
    }
}

package server;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import model.GameData;
import model.GamesList;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {
    GameService gameService;
    public GameHandler(GameService gameService){
        this.gameService = gameService;
    }

    public Object listGames(Request request, Response response) throws UnauthorizedException{
        try {
            String authToken = request.headers("authorization");

            if (authToken == null || authToken.trim().isEmpty()) {
                response.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            try {
                GamesList gameList = new GamesList(gameService.listGames(authToken));
                response.status(200);
                return new Gson().toJson(gameList);
            } catch (UnauthorizedException e) {
                String msg = e.getMessage();
                if (msg != null && msg.toLowerCase().contains("unauthorized")) {
                    response.status(401);
                    return "{ \"message\": \"Error: unauthorized\" }";
                } else {
                    response.status(500);
                    return "{ \"message\": \"Error: " + msg + "\" }";
                }
            }

        } catch (Exception e) {
            response.status(500);
            return "{ \"message\": \"Error: " + (e.getMessage() != null ? e.getMessage() : "internal server error") + "\" }";
        }
    }

    public Object createGame(Request request, Response response) throws BadRequestException, UnauthorizedException{
        try {
            if (!request.body().contains("\"gameName\":")) {
                response.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            GameData gameData = new Gson().fromJson(request.body(), GameData.class);
            String authToken = request.headers("authorization");

            if (authToken == null || authToken.trim().isEmpty()) {
                response.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            try {
                int gameID = gameService.createGame(authToken, gameData.gameName());
                response.status(200);
                return "{ \"gameID\": %d }".formatted(gameID);
            } catch (BadRequestException e) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("already taken")) {
                    response.status(403);
                    return "{ \"message\": \"Error: already taken\" }";
                } else {
                    response.status(500);
                    return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
                }
            }

        } catch (Exception e) {
            response.status(500);
            return "{ \"message\": \"Error: " + (e.getMessage() != null ? e.getMessage() : "internal server error") + "\" }";
        }
    }

    public Object joinGame(Request req, Response resp) throws BadRequestException, UnauthorizedException {
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
                // Only return 401 if the message explicitly indicates an auth issue
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

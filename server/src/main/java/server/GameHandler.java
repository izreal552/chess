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
        String authToken = request.headers("authorization");
        GamesList gameList = new GamesList(gameService.listGames(authToken));
        response.status(200);
        return new Gson().toJson(gameList);
    }

    public Object createGame(Request request, Response response) throws BadRequestException, UnauthorizedException{
        if(!request.body().contains("\"gameName\":")){
            throw new BadRequestException("No gameName given");
        }

        GameData gameData = new Gson().fromJson(request.body(), GameData.class);
        String authToken = request.headers("authorization");
        int gameID = gameService.createGame(authToken, gameData.gameName());
        response.status(200);
        return "{ \"gameID\": %d }".formatted(gameID);
    }

    public Object joinGame(Request req, Response resp) throws BadRequestException, UnauthorizedException {

        if (!req.body().contains("\"gameID\":")) {
            throw new BadRequestException("No gameID provided");
        }

        String authToken = req.headers("authorization");
        record JoinGameData(String playerColor, int gameID) {}
        JoinGameData joinData = new Gson().fromJson(req.body(), JoinGameData.class);
        boolean joinSuccess =  gameService.joinGame(authToken, joinData.gameID(), joinData.playerColor());

        if (!joinSuccess) {
            resp.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }

        resp.status(200);
        return "{}";
    }

}

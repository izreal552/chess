package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public HashSet<GameData> listGames(String authToken) throws UnauthorizedException{
        try{
            authDAO.getAuth(authToken);
        } catch (DataAccessException error) {
            throw new UnauthorizedException();
        }
        return gameDAO.listGames();
    }

    public GameData getGame(String authToken, int gameID) throws UnauthorizedException, BadRequestException {
        try{
            authDAO.getAuth(authToken);
        }catch (DataAccessException error){
            throw new UnauthorizedException();
        }
        try{
            return gameDAO.getGame(gameID);
        }catch (DataAccessException error){
            throw new BadRequestException(error.getMessage());
        }
    }

    public void updateGame(String authToken, GameData gameData) throws UnauthorizedException, BadRequestException{
        try{
            authDAO.getAuth(authToken);
        } catch (DataAccessException error) {
            throw new UnauthorizedException();
        }

        try{
            gameDAO.updateGame(gameData);
        }catch (DataAccessException error){
            throw new BadRequestException(error.getMessage());
        }
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException, BadRequestException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }

        int gameID;
        do { // Get random gameIDs until the gameID is not already in use
            gameID = ThreadLocalRandom.current().nextInt(1, 10000);
        } while (gameDAO.gameExists(gameID));

        try {
            ChessGame game = new ChessGame();
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            gameDAO.createGame(new GameData(gameID, null, null, gameName, game));
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        return gameID;
    }

    public boolean joinGame(String authToken, int gameID, String color) throws UnauthorizedException, BadRequestException {
        AuthData authData;
        GameData gameData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }

        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();

        if (Objects.equals(color, "WHITE")) {
            if (whiteUser != null && !whiteUser.equals(authData.username())) return false; // Spot taken by someone else
            else whiteUser = authData.username();
        } else if (Objects.equals(color, "BLACK")) {
            if (blackUser != null && !blackUser.equals(authData.username())) return false; // Spot taken by someone else
            else blackUser = authData.username();
        } else {throw new BadRequestException("%s is not a valid team color".formatted(color));}

        try {
            gameDAO.updateGame(new GameData(gameID, whiteUser, blackUser, gameData.gameName(), gameData.game()));
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
        return true;
    }

    public void clear() {
        gameDAO.clear();
    }

}

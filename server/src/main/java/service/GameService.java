package service;


import dataaccess.*;
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

    public HashSet<GameData> listGames(String authToken) throws DataAccessException{
        try{
            authDAO.getAuth(authToken);
        } catch (DataAccessException error) {
            if(error.getMessage().contains("failed")) {
                throw new UnauthorizedException("failed");
            }
            throw new UnauthorizedException("invalid");
        }
        return gameDAO.listGames();
    }

    public GameData getGame(String authToken, int gameID) throws DataAccessException {
        try{
            authDAO.getAuth(authToken);
        }catch (DataAccessException error){
            throw new UnauthorizedException("Does not exist");
        }
        try{
            return gameDAO.getGame(gameID);
        }catch (DataAccessException error){
            throw new BadRequestException(error.getMessage());
        }
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        authDAO.getAuth(authToken);
        int gameID;
        do {
            gameID = ThreadLocalRandom.current().nextInt(1, 10000); // in case the game ID is in use, will find another one
        } while (gameDAO.gameExists(gameID));

        gameDAO.createGame(new GameData(gameID, null, null, gameName, null));
        return gameID;
    }

    public boolean joinGame(String authToken, int gameID, String color) throws DataAccessException {
        AuthData authData;
        GameData gameData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
//            System.out.println(e);
            if(e.getMessage().contains("Auth")) {
                throw new UnauthorizedException("auth");
            }
            throw new UnauthorizedException("invalid");
        }

        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();

        if (Objects.equals(color, "WHITE")) {
            if (whiteUser != null ) {
                return false; // Spot taken by someone else
            } else {
                whiteUser = authData.username();
            }
        } else if (Objects.equals(color, "BLACK")) {
            if (blackUser != null ) {
                return false;
            } // Spot taken by someone else
            else {
                blackUser = authData.username();
            }
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
        authDAO.clear();
    }

}

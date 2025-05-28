package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {
    HashSet<GameData> db;

    public MemoryGameDAO(){
        db = HashSet.newHashSet(16);
    }

    @Override
    public HashSet<GameData> listGames() {
        return db;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        db.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for(GameData game: db){
            if(game.gameID() == gameID){
                return game;
            }
        }
        throw new DataAccessException("Game: " + gameID + " not found");
    }

    @Override
    public boolean gameExists(int gameID) {
        for(GameData game: db){
            if(game.gameID() == gameID){
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try{
            db.remove(getGame(game.gameID()));
            db.add(game);
        } catch (DataAccessException error){
            db.add(game);
        }
    }

    @Override
    public void clear() {
        db = HashSet.newHashSet(16);
    }

    @Override
    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        db.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
    }
}

package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void addAuth(AuthData authData) throws  DataAccessException;
    void delAuth(String authToken) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void clear();
    void addAuth(String authToken, String username) throws DataAccessException;
}

package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void addAuth(AuthData authData);
    void delAuth(String authToken);
    AuthData getAuth(String authToken) throws DataAccessException;
    void clear();
}

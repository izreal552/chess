package service;


import dataaccess.DataAccessException;
import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData userData) throws BadRequestException, DataAccessException {
        try{
            userDAO.createUser(userData);
        } catch (DataAccessException error){
            throw new BadRequestException(error.getMessage());
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        authDAO.addAuth(authData);
        return authData;
    }

    public AuthData loginUser(UserData userData) throws UnauthorizedException, DataAccessException, BadRequestException {
        if (userData == null || userData.username() == null || userData.password() == null) {
            throw new BadRequestException("Missing required fields");
        }

        // throw DataAccessException
        boolean isAuthenticated = userDAO.authUser(userData.username(), userData.password());
        if (!isAuthenticated) {
            throw new DataAccessException("Invalid credentials");
        }

        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(authToken, userData.username());

        return new AuthData(userData.username(), authToken);
    }

    public void logoutUser(String authToken) throws UnauthorizedException, DataAccessException {
        try{
            authDAO.getAuth(authToken);
            if (authToken == null || authToken.isBlank()) {
                throw new DataAccessException("does not exist");
            }
        }catch (DataAccessException error){
            throw new DataAccessException("Database operation failed: " + error.getMessage());
        }
        authDAO.delAuth(authToken);

    }

    public void clear(){
        userDAO.clear();
        authDAO.clear();
    }
}

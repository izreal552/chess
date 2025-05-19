package service;

import dataAccess.*;
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

    public AuthDAO createUser(UserData userData) throws BadRequestException{
        try{
            userDAO.createUser(userData);
        } catch (DataAccessException error){
            throw new BadRequestException(error.getMessage());
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        authDAO.addAuth(authData);
        return authDAO;
    }

    public AuthData loginUser(UserData userData) throws UnauthorizedException{
        boolean userAuth;
        try{
            userAuth = userDAO.authUser(userData.username(), userData.password());
        }catch(DataAccessException error){
            throw new UnauthorizedException();
        }
        if(userAuth){
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(userData.username(), authToken);
            authDAO.addAuth(authData);
            return authData;
        }
        else{
            throw new UnauthorizedException();
        }
    }

    public void logoutUser(String authToken) throws UnauthorizedException{
        try{
            authDAO.getAuth(authToken);
        }catch (DataAccessException error){
            throw new UnauthorizedException();
        }
        authDAO.delAuth(authToken);
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException{
        try{
            return authDAO.getAuth(authToken);
        }catch (DataAccessException error) {
            throw new UnauthorizedException();
        }
    }

    public void clear(){
        userDAO.clear();
        authDAO.clear();
    }
}

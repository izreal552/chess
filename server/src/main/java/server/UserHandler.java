package server;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public Object register(Request request, Response response) throws BadRequestException {
        UserData userData = new Gson().fromJson(request.body(), UserData.class);

        if(userData.username() == null || userData.password() == null || userData.email() == null){
            throw new BadRequestException("No username/password given");
        }

        try{
            AuthData authData = userService.createUser(userData);
            response.status(200);
            return new Gson().toJson(authData);
        }catch (BadRequestException error){
            response.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }
    }

    public Object login(Request request, Response response) throws UnauthorizedException, BadRequestException{
        UserData userData = new Gson().fromJson(request.body(), UserData.class);
        if(userData.username() == null || userData.password() == null){
            throw new BadRequestException("No username/password given");
        }
        AuthData authData= userService.loginUser(userData);

        response.status(200);
        return new Gson().toJson(authData);
    }

    public Object logout(Request request, Response response) throws  UnauthorizedException{
        String authToken = request.headers("authorization");
        userService.logoutUser(authToken);
        response.status(200);
        return "{}";
    }
}

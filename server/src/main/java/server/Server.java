package server;

import dataAccess.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    static UserService userService;
    static GameService gameService;

    UserHandler userHandler;
    GameHandler gameHandler;

    public Server() {

        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);
        Spark.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);
        Spark.exception(Exception.class, this::genericExceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request request, Response response) {
        userService.clear();
        gameService.clear();
        response.status(200);
        return "{}";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void badRequestExceptionHandler(BadRequestException ex, Request request, Response response) {
        response.status(400);
        response.body("{ \"message\": \"Error: bad request\" }");
    }

    private void unauthorizedExceptionHandler(UnauthorizedException ex, Request request, Response response) {
        response.status(401);
        response.body("{ \"message\": \"Error: unauthorized\" }");
    }

    private void genericExceptionHandler(Exception ex, Request request, Response response) {
        response.status(500);
        response.body("{ \"message\": \"Error: %s\" }".formatted(ex.getMessage()));
    }
}

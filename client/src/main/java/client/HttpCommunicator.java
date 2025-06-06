package client;

import model.GameData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;

public class HttpCommunicator {

    String baseURL;
    ServerFacade facade;

    public HttpCommunicator(ServerFacade facade, String serverDomain) {
        baseURL = "http://" + serverDomain;
        this.facade = facade;
    }

    public boolean register(String username, String password, String email) {
        return true;
    }

    /**
     *
     * @param username
     * @param password
     * @return success
     */
    public boolean login(String username, String password) {
        return true;
    }

    public boolean logout() {
        return true;
    }

    public int createGame(String gameName) {
        return Integer.parseInt(null);
    }

    public HashSet<GameData> listGames() {
        return null;
    }

    public boolean joinGame(int gameId, String playerColor) {

        return Boolean.parseBoolean(null);
    }

    private Map request (String method, String endpoint) {
        return null;
    }

    private Map request(String method, String endpoint, String body) {

        return null;
    }

    private HttpURLConnection makeConnection(String method, String endpoint, String body) throws URISyntaxException, IOException {

        return null;
    }

    private String requestString(String method, String endpoint) {
        return requestString(method, endpoint, null);
    }

    private String requestString(String method, String endpoint, String body) {

        return null;
    }

    private String readerToString(InputStreamReader reader) {
        return null;
    }


}
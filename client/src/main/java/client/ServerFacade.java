package client;

import com.google.gson.Gson;
import model.GameData;
import model.GamesList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;

public class ServerFacade {
    String baseURL = "http://localhost:8080";
    String authToken;

    public ServerFacade() {
    }

    public ServerFacade(String url) {
        baseURL = url;
    }

    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        var jsonBody = new Gson().toJson(body);
        Map response = request("POST", "/user", jsonBody);
        if (response.containsKey("Error")) {
            return false;
        }
        authToken = (String) response.get("authToken");
        return true;
    }

    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/session", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = (String) resp.get("authToken");
        return true;
    }

    public boolean logout() {
        Map resp = request("DELETE", "/session", null, true);
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = null;
        return true;
    }

    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/game", jsonBody);
        if (resp.containsKey("Error")) {
            return -1;
        }
        double gameID = (double) resp.get("gameID");
        return (int) gameID;
    }

    public HashSet<GameData> listGames() {
        String resp = requestRaw("GET", "/game", null);
        if (resp.contains("Error")) {
            return new HashSet<>();
        }
        GamesList games = new Gson().fromJson(resp, GamesList.class);
        return games.games();
    }

    public boolean joinGame(int gameId, String playerColor) {
        Map body = (playerColor != null)
                ? Map.of("gameID", gameId, "playerColor", playerColor)
                : Map.of("gameID", gameId);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("PUT", "/game", jsonBody);
        return !resp.containsKey("Error");
    }

    private Map request(String method, String endpoint, String body) {
        return request(method, endpoint, body, false);
    }

    private Map request(String method, String endpoint) {
        return request(method, endpoint, null);
    }

    private Map request(String method, String endpoint, String body, boolean expectEmptyResponse) {
        try {
            HttpURLConnection http = setupConnection(method, endpoint, body);
            if (http.getResponseCode() == 401) {
                return Map.of("Error", 401);
            }
            if (expectEmptyResponse) {
                return Map.of("Success", true);
            }
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                return new Gson().fromJson(inputStreamReader, Map.class);
            }
        } catch (URISyntaxException | IOException e) {
            return Map.of("Error", e.getMessage());
        }
    }

    private String requestRaw(String method, String endpoint, String body) {
        try {
            HttpURLConnection http = setupConnection(method, endpoint, body);
            if (http.getResponseCode() == 401) {
                return "Error: 401";
            }
            try (InputStream respBody = http.getInputStream()) {
                return readerToString(new InputStreamReader(respBody));
            }
        } catch (URISyntaxException | IOException e) {
            return String.format("Error: %s", e.getMessage());
        }
    }

    private HttpURLConnection setupConnection(String method, String endpoint, String body) throws URISyntaxException, IOException {
        URI uri = new URI(baseURL + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        if (authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }
        if (body != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
        http.connect();
        return http;
    }

    private String readerToString(InputStreamReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int ch; (ch = reader.read()) != -1; ) {
            sb.append((char) ch);
        }
        return sb.toString();
    }
}

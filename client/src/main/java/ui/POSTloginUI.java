package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class POSTloginUI {
    ServerFacade server;
    List<GameData> games;

    public POSTloginUI(ServerFacade server) {
        this.server = server;
        games = new ArrayList<>();
    }

    public void run() {
        boolean loggedIn = true;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        while (loggedIn) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "quit":
                    return;
                case "help":
                    printHelpMenu();
                    break;
                case "logout":
                    server.logout();
                    loggedIn = false;
                    break;
                case "list":
                    refreshGames();
                    printGames();
                    break;
                case "create":
                    if (input.length != 2) {
                        out.println("Please provide a name");
                        printCreate();
                        break;
                    }
                    int gameID = server.createGame(input[1]);
                    out.printf("Created game, ID: %d%n", gameID);
                    break;
                case "join":
                    if (input.length != 3) {
                        out.println("Usage: join <LIST_ID> [WHITE|BLACK]");
                        out.println("Note: Use the LIST_ID (first column) not the gameID");
                        break;
                    }
                    try {
                        refreshGames();
                        int listIndex = Integer.parseInt(input[1]);
                        GameData game = games.get(listIndex);
                        if (server.joinGame(game.gameID(), input[2].toUpperCase())) {
                            out.println("Successfully joined game " + game.gameName());

                            new BoardPrinter(game.game().getBoard()).printBoard();
                            //
                        } else {
                            out.println("Failed to join game");
                        }
                    } catch (Exception e) {
                        out.println("Incorrect Usage: " + e.getMessage());
                        out.println("Usage: join <LIST_ID> [WHITE|BLACK]");
                        out.println("Note: Use the LIST_ID (first column) not the gameID");
                    }
                    break;
                case "observe":
                    if (input.length != 2) {
                        out.println("Please provide a game ID");
                        printObserve();
                        break;
                    }
                    int listIndex = Integer.parseInt(input[1]);
                    if (listIndex < 0 || listIndex >= games.size()) {
                        out.println("Invalid game index. Use the ID from the 'list' command.");
                        break;
                    }
                    GameData observeGame = games.get(Integer.parseInt(input[1]));
                    if (server.joinGame(observeGame.gameID(), null)) {
                        out.println("You are now observing game "+ observeGame.gameName());
                        new BoardPrinter(observeGame.game().getBoard()).printBoard();
                        break;
                    } else {
                        out.println("Game does not exist");
                        printObserve();
                        break;
                    }
                default:
                    out.println("Command not recognized, please try again");
                    printHelpMenu();
                    break;
            }
        }

        PREloginUI prEloginUI = new PREloginUI(server);
        prEloginUI.run();
    }

    private String[] getUserInput() {
        out.print("\n[LOGGED IN] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void refreshGames() {
        games = new ArrayList<>();
        HashSet<GameData> gameList = server.listGames();
        games.addAll(gameList);
        if(games.isEmpty()){
            out.println("There are currently no games");
        }
    }

    private void printGames() {
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            out.printf("%d -- Game Name: %s  |  White User: %s  |  Black User: %s %n", i, game.gameName(), whiteUser, blackUser);
        }
    }

    private void printHelpMenu() {
        printCreate();
        out.println("list - list all games");
        printJoin();
        printObserve();
        out.println("logout - log out of current user");
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printCreate() {
        out.println("create <NAME> - create a new game");
    }

    private void printJoin() {
        out.println("join <ID> [WHITE|BLACK] - join a game as color");
    }

    private void printObserve() {
        out.println("observe <ID> - observe a game");
    }

}

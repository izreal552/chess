package ui;

import client.ServerFacade;

import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class PREloginUI {
    ServerFacade server;
    POSTloginUI posTloginUI;

    public PREloginUI(ServerFacade server) {
        this.server = server;
        posTloginUI = new POSTloginUI(server);
    }


    private String[] getUserInput() {
        out.print("\n[LOGGED OUT] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void printHelpMenu() {
        printRegister();
        printLogin();
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printRegister() {
        out.println("register <USERNAME> <PASSWORD> <EMAIL> - create a new user");
    }

    private void printLogin() {
        out.println("login <USERNAME> <PASSWORD> - login to an existing user");
    }


}

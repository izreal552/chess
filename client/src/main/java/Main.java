import chess.*;
import client.ServerFacade;
import ui.PREloginUI;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client: ");
        ServerFacade server = new ServerFacade();

        PREloginUI prelogin = new PREloginUI(server);
        prelogin.run();
        System.out.println("Exited");
    }
}
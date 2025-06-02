import chess.*;
import client.ServerFacade;
import ui.PREloginUI;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ServerFacade server = new ServerFacade();

        PREloginUI prelogin = new PREloginUI(server);
        prelogin.run();
        System.out.println("Exited");
    }
}
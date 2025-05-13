package chess.moveCalcs;

import chess.*;

import java.util.HashSet;


public class BishopMoveCalc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos) {
        int currX = pos.getColumn();
        int currY = pos.getRow();
        int[][] possibleMovement = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};

        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();
        return MoveCalc.directionalMoves(board, pos, possibleMovement, currY, currX, team);
    }
}

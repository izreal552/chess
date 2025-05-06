package chess.MoveCalcs;

import chess.*;

import java.util.HashSet;


public class BishopMoveCalc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos) {
        int curr_X = pos.getColumn();
        int curr_Y = pos.getRow();
        int[][] possibleMovement = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};

        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();
        return MoveCalc.directionalMoves(board, pos, possibleMovement, curr_Y, curr_X, team);
    }
}

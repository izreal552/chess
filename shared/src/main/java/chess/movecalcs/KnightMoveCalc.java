package chess.movecalcs;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMoveCalc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos) {
        int currX = pos.getColumn();
        int currY = pos.getRow();
        int[][] possibleMovement = {{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};

        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();
        return MoveCalc.singleMovement(board, pos, possibleMovement, currY, currX, team);
    }
}

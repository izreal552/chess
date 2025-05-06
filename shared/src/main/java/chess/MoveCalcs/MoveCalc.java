package chess.MoveCalcs;

import chess.*;

import java.util.HashSet;

public class MoveCalc {
    //clean slate
//    static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos) {
//        return null;
//    }

    //Is the move within the constraints of the board
    static boolean isValid(ChessPosition position) {
        return (position.getRow() >= 1 && position.getRow() <= 8) &&
                (position.getColumn() >= 1 && position.getColumn() <= 8);
    }

    static HashSet<ChessMove> directionalMoves(ChessBoard board, ChessPosition pos, int[][] possible_Movement, int curr_Y, int curr_X, ChessGame.TeamColor team) {
        HashSet<ChessMove> moves = new HashSet<>();
        for (int[] dir : possible_Movement) {
            boolean blocked = false;
            int i = 1;
            while (!blocked) {
                ChessPosition newPos = new ChessPosition(curr_Y + dir[1] * i, curr_X + dir[0] * i);
                if (!isValid(newPos)) {
                    blocked = true;
                }
                else if (board.getPiece(newPos) == null) {
                    moves.add(new ChessMove(pos, newPos, null));
                }
                else if (board.getPiece(newPos).getTeamColor() != team) {
                    moves.add(new ChessMove(pos, newPos, null));
                    blocked = true;
                }
                else if (board.getPiece(newPos).getTeamColor() == team) {
                    blocked = true;
                }
                i++;
            }
        }
        return moves;

    }

    static HashSet<ChessMove> singleMovement(ChessBoard board, ChessPosition pos, int[][] possible_Movement, int curr_Y, int curr_X, ChessGame.TeamColor team){
        HashSet<ChessMove> moves = new HashSet<>();
        for (int[] move : possible_Movement) {
            ChessPosition newPos = new ChessPosition(curr_Y + move[1], curr_X + move[0]);

            if (isValid(newPos)) {
                ChessPiece targetPiece = board.getPiece(newPos);

                if (targetPiece == null) {
                    moves.add(new ChessMove(pos, newPos, null));
                } else if (targetPiece.getTeamColor() != team) {
                    moves.add(new ChessMove(pos, newPos, null)); // Capture
                }
            }
        }

        return moves;
    }
}

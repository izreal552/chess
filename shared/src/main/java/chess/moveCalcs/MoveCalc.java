package chess.moveCalcs;

import chess.*;

import java.util.HashSet;

public class MoveCalc {

    //Is the move within the constraints of the board
    static boolean isValid(ChessPosition position) {
        return (position.getRow() >= 1 && position.getRow() <= 8) &&
                (position.getColumn() >= 1 && position.getColumn() <= 8);
    }

    static HashSet<ChessMove> directionalMoves(ChessBoard board, ChessPosition pos, int[][] possibleMovement,
                                               int currY, int currX, ChessGame.TeamColor team) {
        HashSet<ChessMove> moves = new HashSet<>();
        for (int[] dir : possibleMovement) {
            boolean blocked = false;
            int i = 1;
            while (!blocked) {
                ChessPosition newPos = new ChessPosition(currY + dir[1] * i, currX + dir[0] * i);
                if (!isValid(newPos)) {
                    blocked = true;
                } else if (board.getPiece(newPos) == null) {
                    moves.add(new ChessMove(pos, newPos, null));
                } else if (board.getPiece(newPos).getTeamColor() != team) {
                    moves.add(new ChessMove(pos, newPos, null));
                    blocked = true;
                } else if (board.getPiece(newPos).getTeamColor() == team) {
                    blocked = true;
                }
                i++;
            }
        }
        return moves;

    }

    static HashSet<ChessMove> singleMovement(ChessBoard board, ChessPosition pos, int[][] possibleMovement,
                                             int currY, int currX, ChessGame.TeamColor team) {
        HashSet<ChessMove> moves = new HashSet<>();
        for (int[] move : possibleMovement) {
            ChessPosition newPos = new ChessPosition(currY + move[1], currX + move[0]);

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

package chess.MoveCalcs;

import chess.*;

import java.util.HashSet;

public class PawnMoveCalc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos) {
        HashSet<ChessMove> moves = new HashSet<>();
        int curr_X = pos.getColumn();
        int curr_Y = pos.getRow();
        ChessPiece.PieceType[] promotionPiece = new ChessPiece.PieceType[]{null};
        ChessGame.TeamColor team = board.getPiece(pos).getTeamColor();

        int moveUp = team == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean promote = (team == ChessGame.TeamColor.WHITE && curr_Y == 7) || (team == ChessGame.TeamColor.BLACK && curr_Y == 2);
        if (promote) {
            promotionPiece = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};
        }

        for (ChessPiece.PieceType promotion : promotionPiece) {
            ChessPosition forwardPosition = new ChessPosition(curr_Y + moveUp, curr_X);
            ChessPosition doublePosition = new ChessPosition(curr_Y + moveUp * 2, curr_X);
            ChessPosition rightCapture = new ChessPosition(curr_Y + moveUp, curr_X + 1);
            ChessPosition leftCapture = new ChessPosition(curr_Y + moveUp, curr_X - 1);

            if (MoveCalc.isValid(doublePosition) &&
                    ((team == ChessGame.TeamColor.WHITE && curr_Y == 2) || (team == ChessGame.TeamColor.BLACK && curr_Y == 7)) &&
                    (board.getPiece(forwardPosition) == null) &&
                    (board.getPiece(doublePosition) == null)) {
                moves.add(new ChessMove(pos, doublePosition, promotion));
            }
            if (MoveCalc.isValid(forwardPosition) && (board.getPiece(forwardPosition) == null)) {
                moves.add(new ChessMove(pos, forwardPosition, promotion));
            }
            if (MoveCalc.isValid(rightCapture) &&
                    (board.getPiece(rightCapture) != null) &&
                    (board.getPiece(rightCapture).getTeamColor() != team)) {
                moves.add(new ChessMove(pos, rightCapture, promotion));
            }
            if (MoveCalc.isValid(leftCapture) &&
                    (board.getPiece(leftCapture) != null) &&
                    (board.getPiece(leftCapture).getTeamColor() != team)) {
                moves.add(new ChessMove(pos, leftCapture, promotion));
            }
        }
        return moves;
    }
}

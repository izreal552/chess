package chess;

import chess.MoveCalcs.*;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor team;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.team = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type){
            case KING -> KingMoveCalc.getMove(board,myPosition);
            case QUEEN -> QueenMoveCalc.getMove(board,myPosition);
            case BISHOP -> BishopMoveCalc.getMove(board,myPosition);
            case KNIGHT -> KnightMoveCalc.getMove(board,myPosition);
            case ROOK -> RookMoveCalc.getMove(board,myPosition);
            case PAWN -> PawnMoveCalc.getMove(board,myPosition);
        };
    }
}

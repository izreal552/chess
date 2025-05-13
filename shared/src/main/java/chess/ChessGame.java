package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;

    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        HashSet<ChessMove> validMovement = new HashSet<>();
        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) piece.pieceMoves(board,startPosition);

        for(ChessMove moves:possibleMoves){
            ChessPiece temp = board.getPiece(moves.getEndPosition());
            board.addPiece(startPosition, null);
            board.addPiece(moves.getEndPosition(), piece);
            if(!isInCheck(piece.getTeamColor())){
                validMovement.add(moves);
            }

            board.addPiece(moves.getEndPosition(),temp);
            board.addPiece(startPosition,piece);
        }
        return validMovement;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece temp = board.getPiece(move.getStartPosition());
        if(temp == null){
            throw new InvalidMoveException();
        }
        boolean isTeamsTurn = getTeamTurn() == temp.getTeamColor();
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if(moves == null){
            throw new InvalidMoveException("No valid moves");
        }

        boolean isValidMove = moves.contains(move);

        if(isValidMove && isTeamsTurn){
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if(move.getPromotionPiece() != null){
                piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getStartPosition(),null);
            board.addPiece(move.getEndPosition(), piece);
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        }
        else {
            throw new InvalidMoveException();
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
//        ChessPosition kingPos = null;
        ChessPosition kingPos = findKingPosition(teamColor);
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = pos;
                    break;
                }
            }
            if (kingPos != null) {break;}
        }

        //Check if any piece can attack the king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null &&
                        piece.getTeamColor() == teamColor &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false; // Not in check, so not in checkmate
        }

        // Iterate through all team pieces to check for any valid escape move (including king capturing)
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return false; // At least one piece has a move that prevents check
                    }
                }
            }
        }

        return true; // No valid moves found, and king is in check
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(teamTurn != teamColor){
            return false;
        }
        if(isInCheck(teamColor)){
            return false;
        }
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);
                Collection<ChessMove> moves;

                if(piece != null && teamColor == piece.getTeamColor()){
                    moves = validMoves(pos);
                    if(moves != null && !moves.isEmpty()){
                        return false;
                    }
                }

            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}

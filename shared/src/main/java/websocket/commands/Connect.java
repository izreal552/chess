package websocket.commands;

public class Connect extends UserGameCommand{
    public enum Role {
        PLAYER,
        OBSERVER
    }

    private final Role role;

    public Connect(String authToken, int gameID, Role role) {
        super(CommandType.CONNECT, authToken, gameID);
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}

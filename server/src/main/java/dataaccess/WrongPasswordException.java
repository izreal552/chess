package dataaccess;

public class WrongPasswordException extends UnauthorizedException {
    public WrongPasswordException(String message) {
        super(message);
    }
}

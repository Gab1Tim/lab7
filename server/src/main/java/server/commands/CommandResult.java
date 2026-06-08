package server.commands;

public class CommandResult {
    private final boolean success;
    private final String message;
    private final String token;

    public CommandResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.token = null;
    }

    public CommandResult(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
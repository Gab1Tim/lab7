package client.auth;

public class AuthManager {
    private static String login;
    private static String password;
    private static String token;
    private static boolean authenticated = false;

    public static void setCredentials(String login, String password) {
        AuthManager.login = login;
        AuthManager.password = password;
    }

    public static String getLogin() {
        return login;
    }

    public static String getPassword() {
        return password;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        AuthManager.token = token;
    }

    public static boolean isAuthenticated() {
        return authenticated;
    }

    public static void setAuthenticated(boolean auth) {
        authenticated = auth;
    }

    public static void clear() {
        login = null;
        password = null;
        token = null;
        authenticated = false;
    }
}
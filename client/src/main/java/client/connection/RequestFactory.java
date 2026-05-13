package client.connection;

import client.auth.AuthManager;
import client.managers.InputManager;
import common.models.Organization;
import common.models.OrganizationType;
import common.network.CommandType;
import common.network.Request;

public class RequestFactory {

    public static Request buildRequest(String commandName) {
        Request request;

        switch (commandName) {
            case "show":
                request = new Request(CommandType.SHOW);
                break;

            case "info":
                request = new Request(CommandType.INFO);
                break;

            case "clear":
                request = new Request(CommandType.CLEAR);
                break;

            case "min_by_name":
                request = new Request(CommandType.MIN_BY_NAME);
                break;

            case "print_unique_annual_turnover":
                request = new Request(CommandType.PRINT_UNIQUE_ANNUAL_TURNOVER);
                break;

            case "insert": {
                request = new Request(CommandType.INSERT);
                int key = InputManager.readInt("Enter key: ");
                Organization organization = InputManager.readOrganization();
                request.setKey(key);
                request.setOrganization(organization);
                break;
            }

            case "update": {
                request = new Request(CommandType.UPDATE);
                long id = InputManager.readInt("Enter id: ");
                Organization organization = InputManager.readOrganization();
                request.setId(id);
                request.setOrganization(organization);
                break;
            }

            case "remove_key": {
                request = new Request(CommandType.REMOVE_KEY);
                int key = InputManager.readInt("Enter key to remove: ");
                request.setKey(key);
                break;
            }

            case "remove_greater_key": {
                request = new Request(CommandType.REMOVE_GREATER_KEY);
                int key = InputManager.readInt("Enter reference key: ");
                request.setKey(key);
                break;
            }

            case "remove_lower": {
                request = new Request(CommandType.REMOVE_LOWER);
                Organization organization = InputManager.readOrganization();
                request.setOrganization(organization);
                break;
            }

            case "filter_greater_than_type": {
                request = new Request(CommandType.FILTER_GREATER_THAN_TYPE);
                String typeStr = InputManager.readLine("Enter organization type: ");
                try {
                    OrganizationType type = OrganizationType.valueOf(typeStr.trim().toUpperCase());
                    request.setOrganizationType(type);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid organization type.");
                    return null;
                }
                break;
            }

            case "help":
                request = new Request(CommandType.HELP);
                break;

            case "login":
                request = new Request(CommandType.LOGIN);
                break;

            case "register":
                request = new Request(CommandType.REGISTER);
                break;

            default:
                return null;
        }

        if (AuthManager.isAuthenticated()) {
            request.setLogin(AuthManager.getLogin());
            request.setPassword(AuthManager.getPassword());
        }

        return request;
    }
}
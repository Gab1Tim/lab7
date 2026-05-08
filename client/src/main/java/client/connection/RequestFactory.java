package client.connection;

import client.managers.InputManager;
import common.models.Organization;
import common.models.OrganizationType;
import common.network.CommandType;
import common.network.Request;

public class RequestFactory {

    public static Request buildRequest(String commandName) {
        switch (commandName) {
            case "show":
                return new Request(CommandType.SHOW);

            case "info":
                return new Request(CommandType.INFO);

            case "clear":
                return new Request(CommandType.CLEAR);

            case "min_by_name":
                return new Request(CommandType.MIN_BY_NAME);

            case "print_unique_annual_turnover":
                return new Request(CommandType.PRINT_UNIQUE_ANNUAL_TURNOVER);

            case "insert": {
                Request request = new Request(CommandType.INSERT);
                int key = InputManager.readInt("Enter key: ");
                Organization organization = InputManager.readOrganization();
                request.setKey(key);
                request.setOrganization(organization);
                return request;
            }

            case "update": {
                Request request = new Request(CommandType.UPDATE);
                long id = InputManager.readInt("Enter id: ");
                Organization organization = InputManager.readOrganization();
                request.setId(id);
                request.setOrganization(organization);
                return request;
            }

            case "remove_key": {
                Request request = new Request(CommandType.REMOVE_KEY);
                int key = InputManager.readInt("Enter key to remove: ");
                request.setKey(key);
                return request;
            }

            case "remove_greater_key": {
                Request request = new Request(CommandType.REMOVE_GREATER_KEY);
                int key = InputManager.readInt("Enter reference key: ");
                request.setKey(key);
                return request;
            }

            case "remove_lower": {
                Request request = new Request(CommandType.REMOVE_LOWER);
                Organization organization = InputManager.readOrganization();
                request.setOrganization(organization);
                return request;
            }

            case "filter_greater_than_type": {
                Request request = new Request(CommandType.FILTER_GREATER_THAN_TYPE);
                String typeStr = InputManager.readLine("Enter organization type: ");
                try {
                    OrganizationType type = OrganizationType.valueOf(typeStr.trim().toUpperCase());
                    request.setOrganizationType(type);
                    return request;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid organization type.");
                    return null;
                }
            }
            case "help":
                return new Request(CommandType.HELP);

            default:
                return null;
        }
    }
}
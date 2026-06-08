package server.auth;

import common.auth.Role;
import common.network.CommandType;


import java.util.Map;


public class PermissionManager {

    private static final Map<CommandType, Role> REQUIRED_ROLES = Map.ofEntries(
            Map.entry(CommandType.SHOW, Role.USER_JUNIOR),
            Map.entry(CommandType.INFO, Role.USER_JUNIOR),
            Map.entry(CommandType.MIN_BY_NAME, Role.USER_JUNIOR),
            Map.entry(CommandType.FILTER_GREATER_THAN_TYPE, Role.USER_JUNIOR),
            Map.entry(CommandType.PRINT_UNIQUE_ANNUAL_TURNOVER, Role.USER_JUNIOR),
            Map.entry(CommandType.HELP, Role.USER_JUNIOR),

            Map.entry(CommandType.INSERT, Role.USER_TEAMLEAD),
            Map.entry(CommandType.UPDATE, Role.USER_TEAMLEAD),
            Map.entry(CommandType.REMOVE_KEY, Role.USER_TEAMLEAD),
            Map.entry(CommandType.CLEAR, Role.USER_TEAMLEAD),
            Map.entry(CommandType.REMOVE_GREATER_KEY, Role.USER_TEAMLEAD),
            Map.entry(CommandType.REMOVE_LOWER, Role.USER_TEAMLEAD),

            Map.entry(CommandType.SHOW_USERS, Role.ADMIN),
            Map.entry(CommandType.CHANGE_USER_ROLE, Role.ADMIN)
    );

    public boolean hasPermission(Role userRole, CommandType commandType) {
        Role requiredRole = REQUIRED_ROLES.get(commandType);
        if (requiredRole == null) {
            return false;
        }
        return userRole.ordinal() <= requiredRole.ordinal();
    }
}
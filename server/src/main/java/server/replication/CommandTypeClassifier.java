package server.replication;

import common.network.CommandType;

import java.util.EnumSet;
import java.util.Set;

public class CommandTypeClassifier {

    private static final Set<CommandType> READ_COMMANDS = EnumSet.of(
            CommandType.SHOW,
            CommandType.INFO,
            CommandType.MIN_BY_NAME,
            CommandType.FILTER_GREATER_THAN_TYPE,
            CommandType.PRINT_UNIQUE_ANNUAL_TURNOVER,
            CommandType.HELP,
            CommandType.LOGIN,
            CommandType.REGISTER
    );

    private static final Set<CommandType> WRITE_COMMANDS = EnumSet.of(
            CommandType.INSERT,
            CommandType.UPDATE,
            CommandType.REMOVE_KEY,
            CommandType.CLEAR,
            CommandType.REMOVE_GREATER_KEY,
            CommandType.REMOVE_LOWER
    );

    public boolean isRead(CommandType type) {
        return READ_COMMANDS.contains(type);
    }

    public boolean isWrite(CommandType type) {
        return WRITE_COMMANDS.contains(type);
    }
}
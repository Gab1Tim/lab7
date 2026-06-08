package server.commands;

import common.network.Request;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

import java.sql.SQLException;

public class ClearCommand implements Command {
    private final CollectionManager collectionManager;
    private final DatabaseCollectionManager dbCollectionManager;

    public ClearCommand(CollectionManager collectionManager,
                        DatabaseCollectionManager dbCollectionManager) {
        this.collectionManager = collectionManager;
        this.dbCollectionManager = dbCollectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clears all organizations owned by the user";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            Long userId = request.getUserId();
            if (userId == null) return new CommandResult(false, "User not authenticated.");

            int removed = dbCollectionManager.clear(userId);
            collectionManager.clearUser(userId);
            return new CommandResult(true, removed + " organization(s) removed.");
        } catch (SQLException e) {
            return new CommandResult(false, "Database error: " + e.getMessage());
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
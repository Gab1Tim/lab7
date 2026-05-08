package server.commands;

import common.models.Organization;
import common.models.OrganizationType;
import common.network.Request;
import server.managers.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

public class FilterGreaterThanTypeCommand implements Command {
    private final CollectionManager collectionManager;

    public FilterGreaterThanTypeCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_greater_than_type";
    }

    @Override
    public String getDescription() {
        return "Shows elements with type greater than the given type";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            OrganizationType type = request.getOrganizationType();
            if (type == null) {
                return new CommandResult(false, "Organization type is required.");
            }

            List<Organization> result = collectionManager.getFilteredGreaterThanType(type);
            if (result.isEmpty()) {
                return new CommandResult(true, "No organizations found with type greater than " + type);
            }

            String message = result.stream()
                    .map(Organization::toString)
                    .collect(Collectors.joining("\n"));

            return new CommandResult(true, message);
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
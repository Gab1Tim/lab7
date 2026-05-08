package server.commands;

import common.network.Request;
import server.managers.CollectionManager;

import java.util.Set;
import java.util.stream.Collectors;

public class PrintUniqueAnnualTurnoverCommand implements Command {
    private final CollectionManager collectionManager;

    public PrintUniqueAnnualTurnoverCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "print_unique_annual_turnover";
    }

    @Override
    public String getDescription() {
        return "Prints all unique annual turnover values";
    }

    @Override
    public CommandResult execute(Request request) {
        try {
            Set<Integer> values = collectionManager.getUniqueAnnualTurnovers();
            if (values.isEmpty()) {
                return new CommandResult(true, "Collection is empty.");
            }

            String message = values.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("\n"));

            return new CommandResult(true, message);
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
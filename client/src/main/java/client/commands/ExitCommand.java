package client.commands;

public class ExitCommand implements Command {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exit the program";
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Exiting program...");
        System.exit(0);
    }
}
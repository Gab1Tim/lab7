package server.commands;

import common.network.Request;

public interface Command {
    String getName();
    String getDescription();
    CommandResult execute(Request request);
}
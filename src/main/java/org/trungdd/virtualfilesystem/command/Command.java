package org.trungdd.virtualfilesystem.command;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private CommandType commandType;
    private List<String> flags;
    private List<String> params;

    public Command() {
        commandType = CommandType.UNKNOWN;
        flags = new ArrayList<>();
        params = new ArrayList<>();
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommand(CommandType commandType) {
        this.commandType = commandType;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}

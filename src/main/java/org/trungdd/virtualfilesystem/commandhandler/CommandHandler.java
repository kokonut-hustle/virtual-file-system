package org.trungdd.virtualfilesystem.commandhandler;

import org.trungdd.virtualfilesystem.command.Command;

public interface CommandHandler {
    public String handle(Command command, String curDir);
}

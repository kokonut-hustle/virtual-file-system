package org.trungdd.virtualfilesystem.commandhandler;

import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.websocket.Message;

public class UKCommandHandler implements CommandHandler {

    @Override
    public String handle(Command command, String curDir) {
        Message message = new Message();
        message.setStatus("success");
        message.pushInfo("My service doesn't support your command, please check again!");
        message.setCurDir(curDir);

        return message.toString();
    }
}

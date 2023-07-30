package org.trungdd.virtualfilesystem.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.command.CommandParser;
import org.trungdd.virtualfilesystem.commandhandler.*;
import org.trungdd.virtualfilesystem.service.FileContentServiceImpl;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private final CommandParser commandParser;

    private final FileMetadataServiceImpl fileMetadataService;

    private final FileContentServiceImpl fileContentService;

    // Use when the server wants to send a message to all clients
    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    public SocketHandler(CommandParser commandParser, FileMetadataServiceImpl fileMetadataService, FileContentServiceImpl fileContentService) {
        this.commandParser = commandParser;
        this.fileMetadataService = fileMetadataService;
        this.fileContentService = fileContentService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        JsonElement jsonElement = new Gson().fromJson(message.getPayload(), JsonElement.class);

        // Check if the 'command' and 'curDir' fields are present in the JSON data
        if (!jsonElement.isJsonObject() || !jsonElement.getAsJsonObject().has("curDir") ||
                !jsonElement.getAsJsonObject().has("command")) {
            System.out.println("Invalid command input.");
            return;
        }

        String text = jsonElement.getAsJsonObject().get("command").getAsString();
        String curDir = jsonElement.getAsJsonObject().get("curDir").getAsString();
        Command parsedCommand = commandParser.parseCommand(text);
        CommandHandler commandHandler;

        if (parsedCommand != null) {
            switch (parsedCommand.getCommandType()) {
                case CD:
                    commandHandler = new CdCommandHandler(fileMetadataService);
                    break;
                case CR:
                    commandHandler = new CrCommandHandler(fileMetadataService, fileContentService);
                    break;
                case CAT:
                    commandHandler = new CatCommandHandler(fileMetadataService, fileContentService);
                    break;
                case LS:
                    commandHandler = new LsCommandHandler(fileMetadataService);
                    break;
                case FIND:
                    commandHandler = new FindCommandHandler(fileMetadataService);
                    break;
                case UP:
                    commandHandler = new UpCommandHandler(fileMetadataService, fileContentService);
                    break;
                case MV:
                    commandHandler = new MvCommandHandler(fileMetadataService);
                    break;
                case RM:
                    commandHandler = new RmCommandHandler(fileMetadataService, fileContentService);
                    break;
                default:
                    commandHandler = new UKCommandHandler();
                    break;
            }

            session.sendMessage(new TextMessage(commandHandler.handle(parsedCommand, curDir)));
        } else {
            System.out.println("Invalid command input.");
        }

        // Do something with the parsed commands, e.g., send them back to the client

        // session.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //the messages will be broadcasted to all users.
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}

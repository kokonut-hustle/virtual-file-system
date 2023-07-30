package org.trungdd.virtualfilesystem.commandhandler;

import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;
import org.trungdd.virtualfilesystem.websocket.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class FindCommandHandler implements CommandHandler {
    private final FileMetadataServiceImpl fileMetadataService;

    public FindCommandHandler(FileMetadataServiceImpl fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    public String handle(Command command, String curDir) {
        Message message = new Message();

        if (command.getParams().size() == 0) {
            message.setStatus("success");
            message.pushInfo("name is required");
            message.setCurDir(curDir);

            return message.toString();
        }

        String name = command.getParams().get(0);
        String folderPath = command.getParams().size() < 2 ? curDir : command.getParams().get(1);

        // Get the folder based on the folderPath
        FileMetadata targetFolder = fileMetadataService.getFileMetadataByPath(folderPath);
        if (targetFolder == null) {
            message.setStatus("success");
            message.pushInfo("Folder not found: " + folderPath);
            message.setCurDir(curDir);

            return message.toString();
        }

        // Perform a recursive search to find items whose names contain the substring `name`
        List<FileMetadata> matchingItems = new ArrayList<>();
        Queue<FileMetadata> metadataQueue = new ArrayDeque<>();
        metadataQueue.add(targetFolder);

        while (!metadataQueue.isEmpty()) {
            FileMetadata currentFolder = metadataQueue.poll();
            if (currentFolder.getName().contains(name)) {
                matchingItems.add(currentFolder);
            }

            if (currentFolder.isDir()) {
                List<FileMetadata> items = fileMetadataService.getFileMetadataByParentId(
                        currentFolder.getId()
                );
                metadataQueue.addAll(items);
            }
        }

        message.setStatus("success");
        for (FileMetadata item : matchingItems) {
            message.pushInfo("name: " + item.getName() +
                    ", path: " + item.getPath() +
                    ", size: " + item.getSize());
        }
        message.setCurDir(folderPath);

        return message.toString();
    }
}

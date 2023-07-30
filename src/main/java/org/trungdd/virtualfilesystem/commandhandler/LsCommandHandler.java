package org.trungdd.virtualfilesystem.commandhandler;

import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;
import org.trungdd.virtualfilesystem.utils.DirectoryUtils;
import org.trungdd.virtualfilesystem.websocket.Message;

import java.util.List;

public class LsCommandHandler implements CommandHandler {

    private final FileMetadataServiceImpl fileMetadataService;

    // Constructor to inject dependencies
    public LsCommandHandler(FileMetadataServiceImpl fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    @Override
    public String handle(Command command, String curDir) {
        // Get the folder path from the command parameters
        String folderPath = command.getParams().isEmpty() ? curDir : command.getParams().get(0);

        // Create the absolute folder path based on the current directory
        String absoluteFolderPath = command.getParams().isEmpty() ? curDir :
                DirectoryUtils.getAbsolutePath(curDir, folderPath);

        Message message = new Message();

        // Retrieve the file metadata for the specified folder path
        FileMetadata parent = fileMetadataService.getFileMetadataByPath(absoluteFolderPath);
        if (parent != null) {
            Long parentId = parent.getId();
            List<FileMetadata> fileMetadataList = fileMetadataService.getFileMetadataByParentId(parentId);

            message.pushInfo("parent folder name: " + parent.getName() +
                    ", created at: " + parent.getCreatedAt() +
                    ", size: " + parent.getSize());
            for (FileMetadata fileMetadata : fileMetadataList) {
                message.pushInfo("child name: " + fileMetadata.getName() +
                        ", created at: " + fileMetadata.getCreatedAt() +
                        ", size: " + fileMetadata.getSize());
            }
            message.setStatus("success");
            message.setCurDir(absoluteFolderPath);
        } else {
            message.setStatus("success");
            message.pushInfo("path not found: " + absoluteFolderPath);
            message.setCurDir(curDir);
        }

        return message.toString();
    }
}

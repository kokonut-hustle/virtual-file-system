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
            if (!parent.isDir()) {
                message.setStatus("success");
                message.pushInfo("path is not a directory: " + absoluteFolderPath);
                message.setCurDir(curDir);
            } else {
                Long parentId = parent.getId();
                List<FileMetadata> fileMetadataList = fileMetadataService.getFileMetadataByParentId(parentId);

                String formatString = "%-15s %-20s %-30s %s";
                String tableHeader = String.format(formatString, "Name", "Type", "Created At", "Size");

                message.pushInfo(tableHeader);

                String parentName = (parent.getName().length() > 10) ?
                        "..." + parent.getName().substring(parent.getName().length() - 10) :
                        parent.getName();
                String parentFolder = String.format(formatString, parentName,
                        "parent folder", parent.getCreatedAt(), parent.getSize());

                message.pushInfo(parentFolder);
                for (FileMetadata fileMetadata : fileMetadataList) {
                    String itemName = (fileMetadata.getName().length() > 10) ?
                            "..." + fileMetadata.getName().substring(fileMetadata.getName().length() - 10) :
                            fileMetadata.getName();

                    if (fileMetadata.isDir()) {
                        message.pushInfo(String.format(formatString, itemName,
                                "folder", fileMetadata.getCreatedAt(),
                                fileMetadata.getSize()));
                    } else {
                        message.pushInfo(String.format(formatString, itemName,
                                "file", fileMetadata.getCreatedAt(),
                                fileMetadata.getSize()));
                    }
                }
                message.setStatus("success");
                message.setCurDir(absoluteFolderPath);
            }
        } else {
            message.setStatus("success");
            message.pushInfo("path not found: " + absoluteFolderPath);
            message.setCurDir(curDir);
        }

        return message.toString();
    }
}

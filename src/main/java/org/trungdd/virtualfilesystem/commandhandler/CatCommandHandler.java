package org.trungdd.virtualfilesystem.commandhandler;

import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.model.FileContent;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.service.FileContentServiceImpl;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;
import org.trungdd.virtualfilesystem.utils.DirectoryUtils;
import org.trungdd.virtualfilesystem.websocket.Message;

public class CatCommandHandler implements CommandHandler {

    private final FileMetadataServiceImpl fileMetadataService;
    private final FileContentServiceImpl fileContentService;

    // Constructor to inject dependencies
    public CatCommandHandler(FileMetadataServiceImpl fileMetadataService, FileContentServiceImpl fileContentService) {
        this.fileMetadataService = fileMetadataService;
        this.fileContentService = fileContentService;
    }

    @Override
    public String handle(Command command, String curDir) {
        // Get the file path from the command parameters
        String filePath = command.getParams().isEmpty() ? null : command.getParams().get(0);

        Message message = new Message();

        if (filePath == null || filePath.equals("")) {
            message.setStatus("success");
            message.pushInfo("Usage: cat <file-path>");
            message.setCurDir(curDir);

            return message.toString();
        }

        // Create the absolute file path based on the current directory
        String absoluteFilePath = DirectoryUtils.getAbsolutePath(curDir, filePath);

        // Check if the file exists and is not a directory
        FileMetadata fileMetadata = fileMetadataService.getFileMetadataByPath(absoluteFilePath);
        if (fileMetadata != null && !fileMetadata.isDir()) {
            // File exists, retrieve its content
            FileContent fileContent = fileContentService.getFileContentById(fileMetadata.getFileContentId());
            if (fileContent != null) {
                message.setStatus("success");
                message.pushInfo(fileContent.getContent());
                message.setCurDir(curDir);

                return message.toString();
            } else {
                message.setStatus("success");
                message.pushInfo("Error retrieving file content.");
                message.setCurDir(curDir);

                return message.toString();
            }
        } else {
            message.setStatus("success");
            message.pushInfo("File not found or it is a directory.");
            message.setCurDir(curDir);

            return message.toString();
        }
    }
}


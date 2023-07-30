package org.trungdd.virtualfilesystem.commandhandler;

import org.springframework.transaction.annotation.Transactional;
import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.command.CommandParser;
import org.trungdd.virtualfilesystem.model.FileContent;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.service.FileContentServiceImpl;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;
import org.trungdd.virtualfilesystem.utils.DirectoryUtils;
import org.trungdd.virtualfilesystem.websocket.Message;

import java.util.List;

public class CrCommandHandler implements CommandHandler {

    private final FileMetadataServiceImpl fileMetadataService;

    private final FileContentServiceImpl fileContentService;

    public CrCommandHandler(FileMetadataServiceImpl fileMetadataService, FileContentServiceImpl fileContentService) {
        this.fileMetadataService = fileMetadataService;
        this.fileContentService = fileContentService;
    }

    @Override
    @Transactional
    public String handle(Command command, String curDir) {
        Message message = new Message();

        List<String> params = command.getParams();
        String targetPath = params.get(0);
        if (params == null || params.isEmpty() || targetPath == null) {
            message.setStatus("success");
            message.pushInfo("need at least 1 param");
            message.setCurDir(curDir);

            return message.toString();
        }
        
        boolean createParentFolders = command.getFlags().contains("p");
        boolean isDataSpecified = command.getParams().size() > 1;

        String absolutePath = DirectoryUtils.getAbsolutePath(curDir, targetPath);
        String nameInput = DirectoryUtils.getNameFromPath(absolutePath);
        if (!CommandParser.isValidName(nameInput)) {
            message.setStatus("success");
            message.pushInfo("name is not valid");
            message.setCurDir(curDir);

            return message.toString();
        }

        if (isDataSpecified) {
            // Check if the path in FileMetadata exists and is not a directory
            FileMetadata existingMetadata = fileMetadataService.getFileMetadataByPath(absolutePath);
            if (existingMetadata != null && !existingMetadata.isDir()) {
                message.setStatus("success");
                message.pushInfo("File already exists at: " + absolutePath);
                message.setCurDir(curDir);
            } else {
                // Create a new file with the specified DATA
                try {
                    FileContent newFileContent = new FileContent(command.getParams().get(1));
                    Long size = Long.valueOf(command.getParams().get(1).length());

                    String parentPath = DirectoryUtils.getParentPath(absolutePath);
                    FileMetadata parent = fileMetadataService.getFileMetadataByPath(parentPath);

                    if (parent == null && !createParentFolders) {
                        message.setStatus("success");
                        message.pushInfo("You should create parent folder first, or add option -p");
                        message.setCurDir(curDir);

                        return message.toString();
                    }

                    // Update from root folder
                    String path = "/";
                    Long parentId = Long.valueOf(-1);
                    while (!path.equals(absolutePath)) {
                        parent = fileMetadataService.getFileMetadataByPath(path);
                        if (parent != null && parent.isDir()) {
                            parent.setSize(parent.getSize() + size);
                            parentId = parent.getId();
                            fileMetadataService.saveFileMetadata(parent);
                        } else {
                            String name = DirectoryUtils.getNameFromPath(path);

                            FileMetadata newParent = new FileMetadata(name, path, size, true, parentId);
                            parentId = fileMetadataService.saveFileMetadata(newParent).getId();
                        }
                        path = DirectoryUtils.nextDir(path, absolutePath);
                    }

                    Long fileContentId = fileContentService.saveFileContent(newFileContent).getId();

                    String name = DirectoryUtils.getNameFromPath(absolutePath);
                    FileMetadata newFileMetadata = new FileMetadata(name, absolutePath, size, false, parentId);
                    newFileMetadata.setFileContentId(fileContentId);
                    fileMetadataService.saveFileMetadata(newFileMetadata);

                    message.setStatus("success");
                    message.pushInfo("File created at: " + absolutePath);
                    message.setCurDir(parentPath);
                    return message.toString();
                } catch (Exception e) {
                    message.setStatus("internal error");
                    message.pushInfo("File was not created because of internal error");
                    message.setCurDir(curDir);
                    return message.toString();
                }
            }
        } else {
            // Create a new directory

            // Check if the path in FileMetadata exists and is a directory
            FileMetadata existingMetadata = fileMetadataService.getFileMetadataByPath(absolutePath);
            if (existingMetadata != null && existingMetadata.isDir()) {
                message.setStatus("success");
                message.pushInfo("Directory already exists at: " + absolutePath);
                message.setCurDir(curDir);
            } else {
                try {
                    String parentPath = DirectoryUtils.getParentPath(absolutePath);
                    FileMetadata parent = fileMetadataService.getFileMetadataByPath(parentPath);

                    if (parent == null && !createParentFolders) {
                        message.setStatus("success");
                        message.pushInfo("You should create parent folder first, or add option -p");
                        message.setCurDir(curDir);

                        return message.toString();
                    }

                    // Update from root folder
                    String path = "/";
                    Long parentId = Long.valueOf(-1);
                    while (!path.equals(absolutePath)) {
                        parent = fileMetadataService.getFileMetadataByPath(path);
                        if (parent != null && parent.isDir()) {
                            parentId = parent.getId();
                        } else {
                            // Create a new directory
                            String name = DirectoryUtils.getNameFromPath(path);

                            FileMetadata newParent = new FileMetadata(name, path, 0L, true, parentId);
                            parentId = fileMetadataService.saveFileMetadata(newParent).getId();
                        }
                        path = DirectoryUtils.nextDir(path, absolutePath);
                    }
                    String name = DirectoryUtils.getNameFromPath(absolutePath);
                    FileMetadata newFileMetadata = new FileMetadata(name, absolutePath, 0L, true, parentId);
                    fileMetadataService.saveFileMetadata(newFileMetadata);

                    message.setStatus("success");
                    message.pushInfo("Directory created at: " + absolutePath);
                    message.setCurDir(parentPath);
                    return message.toString();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    message.setStatus("internal error");
                    message.pushInfo("Directory was not created because of internal error");
                    message.setCurDir(curDir);
                    return message.toString();
                }
            }
        }

        return message.toString();
    }
}

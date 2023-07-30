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

public class UpCommandHandler implements CommandHandler {
    private final FileMetadataServiceImpl fileMetadataService;
    private final FileContentServiceImpl fileContentService;

    public UpCommandHandler(FileMetadataServiceImpl fileMetadataService, FileContentServiceImpl fileContentService) {
        this.fileMetadataService = fileMetadataService;
        this.fileContentService = fileContentService;
    }

    @Transactional
    public String handle(Command command, String curDir) {
        Message message = new Message();

        List<String> params = command.getParams();
        if (params.size() < 2) {
            message.setStatus("success");
            message.pushInfo("both path and name are needed");
            message.setCurDir(curDir);

            return message.toString();
        }

        String path = params.get(0);
        String name = params.get(1);
        String data = params.size() > 2 ? params.get(2) : null;

        if (!CommandParser.isValidName(name)) {
            message.setStatus("success");
            message.pushInfo("name is not valid " + name);
            message.setCurDir(curDir);

            return message.toString();
        }

        String absolutePath = DirectoryUtils.getAbsolutePath(curDir, path);

        FileMetadata item = fileMetadataService.getFileMetadataByPath(absolutePath);
        if (item == null) {
            message.setStatus("success");
            message.pushInfo("path is not found: " + absolutePath);
            message.setCurDir(curDir);

            return message.toString();
        }

        String parentPath = DirectoryUtils.getParentPath(absolutePath);
        String newPath = parentPath.equals("/") ? parentPath + name : parentPath + "/" + name;

        // Update the content and size if newData is provided
        if (data != null && !item.isDir()) {
            FileContent fileContent = fileContentService.getFileContentById(item.getFileContentId());
            fileContent.setContent(data);
            fileContentService.saveFileContent(fileContent);

            Long oldSize = Long.valueOf(item.getSize());
            Long newSize = Long.valueOf(data.length());

            String curPath = "/";
            while (!curPath.equals(absolutePath)) {
                FileMetadata fileMetadata = fileMetadataService.getFileMetadataByPath(curPath);
                if (fileMetadata != null && fileMetadata.isDir()) {
                    fileMetadata.setSize(fileMetadata.getSize() + newSize - oldSize);
                    fileMetadataService.saveFileMetadata(fileMetadata);
                }
                curPath = DirectoryUtils.nextDir(curPath, absolutePath);
            }
        }

        // Update the name and path of the item
        item.setName(name);
        item.setPath(newPath);
        item.setSize(Long.valueOf(data.length()));
        fileMetadataService.saveFileMetadata(item);

        message.setStatus("success");
        message.pushInfo("update success");
        message.setCurDir(curDir);

        return message.toString();
    }
}

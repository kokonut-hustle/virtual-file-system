package org.trungdd.virtualfilesystem.commandhandler;

import org.springframework.transaction.annotation.Transactional;
import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.model.FileContent;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.service.FileContentServiceImpl;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;
import org.trungdd.virtualfilesystem.utils.DirectoryUtils;
import org.trungdd.virtualfilesystem.websocket.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RmCommandHandler implements CommandHandler {
    private final FileMetadataServiceImpl fileMetadataService;
    private final FileContentServiceImpl fileContentService;

    public RmCommandHandler(FileMetadataServiceImpl fileMetadataService, FileContentServiceImpl fileContentService) {
        this.fileMetadataService = fileMetadataService;
        this.fileContentService = fileContentService;
    }

    @Transactional
    public String handle(Command command, String curDir) {
        Message message = new Message();
        List<String> params = command.getParams();

        if (params.size() < 1) {
            message.setStatus("success");
            message.pushInfo("need at least 1 param");
            message.setCurDir(curDir);

            return message.toString();
        }

        for (String path : params) {
            String absolutePath = DirectoryUtils.getAbsolutePath(curDir, path);
            FileMetadata item = fileMetadataService.getFileMetadataByPath(absolutePath);

            if (item == null) {
                message.pushInfo("item not found: " + absolutePath);
            } else {
                // Decrease size of parents
                Long size = item.getSize();
                String curPath = "/";
                while (!curPath.equals(absolutePath)) {
                    FileMetadata fileMetadata = fileMetadataService.getFileMetadataByPath(curPath);
                    if (fileMetadata != null && fileMetadata.isDir()) {
                        fileMetadata.setSize(fileMetadata.getSize() - size);
                        fileMetadataService.saveFileMetadata(fileMetadata);
                    }
                    curPath = DirectoryUtils.nextDir(curPath, absolutePath);
                }

                if (item.isDir()) {
                    // Delete folder and its child recursively
                    List<FileMetadata> metadataList = new ArrayList<>();
                    List<FileContent> contentList = new ArrayList<>();
                    Queue<FileMetadata> metadataQueue = new ArrayDeque<>();
                    metadataQueue.add(item);

                    while (!metadataQueue.isEmpty()) {
                        FileMetadata currentFolder = metadataQueue.poll();
                        metadataList.add(currentFolder);

                        if (currentFolder.isDir()) {
                            List<FileMetadata> items = fileMetadataService.getFileMetadataByParentId(
                                    currentFolder.getId()
                            );
                            metadataQueue.addAll(items);
                        } else {
                            FileContent fileContent = fileContentService.getFileContentById(currentFolder.getFileContentId());
                            contentList.add(fileContent);
                        }
                    }

                    for (FileMetadata fileMetadata : metadataList) {
                        fileMetadataService.deleteFileMetadataById(fileMetadata.getId());
                    }

                    for (FileContent fileContent : contentList) {
                        fileContentService.deleteFileContentById(fileContent.getId());
                    }
                } else {
                    // Delete file
                    fileContentService.deleteFileContentById(item.getFileContentId());
                    fileMetadataService.deleteFileMetadataById(item.getId());
                }
            }
        }

        message.pushInfo("remove done");
        message.setCurDir("/");
        return message.toString();
    }
}

package org.trungdd.virtualfilesystem.commandhandler;

import org.springframework.transaction.annotation.Transactional;
import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;
import org.trungdd.virtualfilesystem.utils.DirectoryUtils;
import org.trungdd.virtualfilesystem.websocket.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MvCommandHandler implements CommandHandler {
    private final FileMetadataServiceImpl fileMetadataService;

    public MvCommandHandler(FileMetadataServiceImpl fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    @Transactional
    public String handle(Command command, String curDir) {
        Message message = new Message();

        List<String> params = command.getParams();
        if (params.size() != 2) {
            message.setStatus("success");
            if (params.size() > 2) {
                message.pushInfo("too many params");
            } else {
                message.pushInfo("need at least 2 params");
            }
            message.setCurDir(curDir);

            return message.toString();
        }

        String source = DirectoryUtils.getAbsolutePath(curDir, params.get(0));
        String dest = DirectoryUtils.getAbsolutePath(curDir, params.get(1));

        if (dest.startsWith(source)) {
            if (dest.length() == source.length() ||
                    dest.charAt(source.length()) == '/') {
                message.setStatus("success");
                message.pushInfo("cannot move to its child folder");
                message.setCurDir(curDir);

                return message.toString();
            }
        }

        FileMetadata sourceMetadata = fileMetadataService.getFileMetadataByPath(source);
        FileMetadata destMetadata = fileMetadataService.getFileMetadataByPath(dest);

        if (sourceMetadata == null || destMetadata == null ||
                !destMetadata.isDir()) {
            message.setStatus("success");
            message.pushInfo("not found source and dest folder");
            message.setCurDir(curDir);

            return message.toString();
        }

        List<FileMetadata> destChildren = fileMetadataService.getFileMetadataByParentId(destMetadata.getId());
        for (FileMetadata child : destChildren) {
            System.out.println("child " + child.getName());
            System.out.println("source " + sourceMetadata.getName());
            if (child.getName().equals(sourceMetadata.getName())) {
                if ((child.isDir() && sourceMetadata.isDir()) ||
                        (!child.isDir() && !sourceMetadata.isDir())) {
                    message.setStatus("success");
                    message.pushInfo("destination dir must not have child item with the same name of source item");
                    message.setCurDir(curDir);

                    return message.toString();
                }
            }
        }

        // Start moving

        // Update size of parent
        Long size = sourceMetadata.getSize();
        String curPath = "/";
        while (!curPath.equals(source)) {
            FileMetadata fileMetadata = fileMetadataService.getFileMetadataByPath(curPath);
            if (fileMetadata != null && fileMetadata.isDir()) {
                fileMetadata.setSize(fileMetadata.getSize() - size);
                fileMetadataService.saveFileMetadata(fileMetadata);
            }
            curPath = DirectoryUtils.nextDir(curPath, source);
        }

        curPath = "/";
        while (!curPath.equals(dest)) {
            FileMetadata fileMetadata = fileMetadataService.getFileMetadataByPath(curPath);
            if (fileMetadata != null && fileMetadata.isDir()) {
                fileMetadata.setSize(fileMetadata.getSize() + size);
                fileMetadataService.saveFileMetadata(fileMetadata);
            }
            curPath = DirectoryUtils.nextDir(curPath, dest);
        }

        destMetadata.setSize(destMetadata.getSize() + size);
        fileMetadataService.saveFileMetadata(destMetadata);

        sourceMetadata.setParentId(destMetadata.getId());

        // Update path of children
        List<FileMetadata> metadataList = new ArrayList<>();
        Queue<FileMetadata> metadataQueue = new ArrayDeque<>();
        metadataQueue.add(sourceMetadata);

        while (!metadataQueue.isEmpty()) {
            FileMetadata currentFolder = metadataQueue.poll();
            metadataList.add(currentFolder);

            if (currentFolder.isDir()) {
                List<FileMetadata> items = fileMetadataService.getFileMetadataByParentId(
                        currentFolder.getId()
                );
                metadataQueue.addAll(items);
            }
        }

        String oldParent = DirectoryUtils.getParentPath(source);
        for (FileMetadata fileMetadata : metadataList) {
            if (oldParent.equals("/")) {
                fileMetadata.setPath(dest.concat(fileMetadata.getPath()));
            } else {
                fileMetadata.setPath(fileMetadata.getPath().replaceFirst(oldParent, dest));
            }
            fileMetadataService.saveFileMetadata(fileMetadata);
        }

        message.setStatus("success");
        message.pushInfo("move success");
        message.setCurDir(dest);

        return message.toString();
    }
}

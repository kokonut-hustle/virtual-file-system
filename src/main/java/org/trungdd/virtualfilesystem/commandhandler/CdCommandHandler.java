package org.trungdd.virtualfilesystem.commandhandler;

import com.google.gson.Gson;
import org.trungdd.virtualfilesystem.command.Command;
import org.trungdd.virtualfilesystem.command.CommandType;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.service.FileMetadataServiceImpl;
import org.trungdd.virtualfilesystem.utils.DirectoryUtils;
import org.trungdd.virtualfilesystem.websocket.Message;

public class CdCommandHandler implements CommandHandler {

    private final FileMetadataServiceImpl fileMetadataService;

    public CdCommandHandler(FileMetadataServiceImpl fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    @Override
    public String handle(Command command, String curDir) {

        Message message = new Message();

        if (command == null || !CommandType.CD.equals(command.getCommandType())) {
            message.setStatus("success");
            message.pushInfo("Invalid cd command");
            message.setCurDir(curDir);
        } else if (command.getParams().isEmpty()) {
            message.setStatus("success");
            message.pushInfo("Need at least 1 param");
            message.setCurDir(curDir);
        } else if (command.getParams().size() > 1) {
            message.setStatus("success");
            message.pushInfo("Too many params");
            message.setCurDir(curDir);
        } else {
            // Valid cd command, perform the logic

            // Target directory
            String directory = command.getParams().get(0);

            String absolutePath = DirectoryUtils.getAbsolutePath(curDir, directory);

            FileMetadata targetDirMetadata = fileMetadataService.getFileMetadataByPath(absolutePath);

            if (targetDirMetadata != null && targetDirMetadata.isDir()) {
                message.pushInfo("cd success");
                message.setCurDir(absolutePath);
            } else {
                message.pushInfo("target directory does not exist: " + absolutePath);
                message.setCurDir(curDir);
            }

            message.setStatus("success");
        }


        Gson gson = new Gson();
        String jsonResult = gson.toJson(message);

        return jsonResult;
    }
}

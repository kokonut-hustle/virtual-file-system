package org.trungdd.virtualfilesystem.service;

import org.springframework.stereotype.Service;
import org.trungdd.virtualfilesystem.model.FileMetadata;

import java.util.List;

@Service
public interface FileMetadataService {

    FileMetadata getFileMetadataByPath(String path);

    List<FileMetadata> getFileMetadataByParentId(Long parentId);

    FileMetadata saveFileMetadata(FileMetadata fileMetadata);

    void deleteFileMetadataById(Long id);

    // Other service methods, if needed
}

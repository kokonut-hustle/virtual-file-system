package org.trungdd.virtualfilesystem.service;

import org.springframework.stereotype.Service;
import org.trungdd.virtualfilesystem.model.FileContent;

@Service
public interface FileContentService {
    FileContent saveFileContent(FileContent fileContent);

    FileContent getFileContentById(Long id);

    void deleteFileContentById(Long id);
    // Add any other methods as needed
}


package org.trungdd.virtualfilesystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trungdd.virtualfilesystem.model.FileContent;
import org.trungdd.virtualfilesystem.repository.FileContentRepository;

@Service
public class FileContentServiceImpl implements FileContentService {

    private final FileContentRepository fileContentRepository;

    @Autowired
    public FileContentServiceImpl(FileContentRepository fileContentRepository) {
        this.fileContentRepository = fileContentRepository;
    }

    @Override
    public FileContent saveFileContent(FileContent fileContent) {
        return fileContentRepository.save(fileContent);
    }

    @Override
    public FileContent getFileContentById(Long id) {
        return fileContentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteFileContentById(Long id) {
        fileContentRepository.deleteById(id);
    }

    // Add any other methods as needed

}

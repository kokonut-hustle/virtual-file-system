package org.trungdd.virtualfilesystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.repository.FileMetadataRepository;

import java.util.List;

@Service
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;

    @Autowired
    public FileMetadataServiceImpl(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }
    
    @Override
    public FileMetadata getFileMetadataByPath(String path) {
        return fileMetadataRepository.findByPath(path);
    }

    @Override
    public List<FileMetadata> getFileMetadataByParentId(Long parentId) {
        return fileMetadataRepository.findByParentId(parentId);
    }

    @Override
    public FileMetadata saveFileMetadata(FileMetadata fileMetadata) {
        return fileMetadataRepository.save(fileMetadata);
    }

    @Override
    public void deleteFileMetadataById(Long id) {
        fileMetadataRepository.deleteById(id);
    }

    // Other service methods, if needed
}

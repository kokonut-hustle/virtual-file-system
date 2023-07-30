package org.trungdd.virtualfilesystem.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.trungdd.virtualfilesystem.model.FileMetadata;
import org.trungdd.virtualfilesystem.repository.FileMetadataRepository;

import java.time.LocalDateTime;

@Service
public class FileMetadataInitService {

    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadataInitService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @PostConstruct
    public void initDefaultRecord() {
        // Check if there are no records in the table
        if (fileMetadataRepository.count() == 0) {
            // Create a default record and save it to the database
            FileMetadata defaultEntity = new FileMetadata();
            defaultEntity.setName("");
            defaultEntity.setPath("/");
            defaultEntity.setSize(Long.valueOf(0L));
            defaultEntity.setDir(true);
            defaultEntity.setParentId(Long.valueOf(-1L));
            defaultEntity.setCreatedAt(LocalDateTime.now());

            fileMetadataRepository.save(defaultEntity);
        }
    }
}


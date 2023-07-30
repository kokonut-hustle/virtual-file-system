package org.trungdd.virtualfilesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trungdd.virtualfilesystem.model.FileMetadata;

import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    FileMetadata findByPath(String path);

    List<FileMetadata> findByParentId(Long parentId);

    // Other custom query methods, if needed
}

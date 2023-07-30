package org.trungdd.virtualfilesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trungdd.virtualfilesystem.model.FileContent;

@Repository
public interface FileContentRepository extends JpaRepository<FileContent, Long> {

    void deleteById(Long id);

    // You can add custom queries or methods if needed
}


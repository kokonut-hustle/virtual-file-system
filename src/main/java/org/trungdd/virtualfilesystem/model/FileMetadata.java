package org.trungdd.virtualfilesystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata", indexes = @Index(name = "index_path", columnList = "path"))
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_dir", nullable = false)
    private boolean isDir;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "file_content_id")
    private Long fileContentId;

    // Constructors, getters, setters, and other methods

    public FileMetadata() {
        // Default constructor required by JPA
    }

    public FileMetadata(String name, String path, Long size, boolean isDir, Long parentId) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDir = isDir;
        this.parentId = parentId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean isDir) {
        this.isDir = isDir;
    }

    public Long getFileContentId() {
        return fileContentId;
    }

    public void setFileContentId(Long fileContentId) {
        this.fileContentId = fileContentId;
    }
}

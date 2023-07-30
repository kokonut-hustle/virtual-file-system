package org.trungdd.virtualfilesystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "file_content")
public class FileContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    // Constructors, getters, and setters

    public FileContent() {
    }

    public FileContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package com.dkp.content.entity;

import jakarta.persistence.*;

/**
 * Content Entity mapping to the 'contents' table in PostgreSQL.
 */
@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType; // e.g. BOOK, ARTICLE, VIDEO, COURSE

    @Column(nullable = false, length = 100)
    private String category; // e.g. PROGRAMMING, DATABASE, AI

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "resource_url", nullable = false, length = 500)
    private String resourceUrl;

    public Content() {
    }

    public Content(Long id, String title, String author, String contentType, String category, String description, String resourceUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.contentType = contentType;
        this.category = category;
        this.description = description;
        this.resourceUrl = resourceUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }
}

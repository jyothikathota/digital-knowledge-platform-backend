package com.dkp.content.dto;

public class ContentResponse {

    private Long id;
    private String title;
    private String author;
    private String contentType;
    private String category;
    private String description;
    private String resourceUrl;

    public ContentResponse() {
    }

    public ContentResponse(Long id, String title, String author, String contentType, String category, String description, String resourceUrl) {
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

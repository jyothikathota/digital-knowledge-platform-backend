package com.dkp.content.dto;

import jakarta.validation.constraints.NotBlank;

public class ContentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Content type is required")
    private String contentType;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotBlank(message = "Resource URL is required")
    private String resourceUrl;

    public ContentRequest() {
    }

    public ContentRequest(String title, String author, String contentType, String category, String description, String resourceUrl) {
        this.title = title;
        this.author = author;
        this.contentType = contentType;
        this.category = category;
        this.description = description;
        this.resourceUrl = resourceUrl;
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

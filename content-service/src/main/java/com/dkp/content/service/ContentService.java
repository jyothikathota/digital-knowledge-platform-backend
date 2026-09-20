package com.dkp.content.service;

import com.dkp.content.dto.ContentRequest;
import com.dkp.content.dto.ContentResponse;
import com.dkp.content.entity.Content;
import com.dkp.content.exception.ContentNotFoundException;
import com.dkp.content.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /**
     * Add new digital academic resource.
     */
    public ContentResponse createContent(ContentRequest request) {
        Content content = new Content();
        content.setTitle(request.getTitle().trim());
        content.setAuthor(request.getAuthor().trim());
        content.setContentType(request.getContentType().trim());
        content.setCategory(request.getCategory().trim());
        content.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        content.setResourceUrl(request.getResourceUrl().trim());

        Content saved = contentRepository.save(content);
        return mapToResponse(saved);
    }

    /**
     * Retrieve all available digital resources.
     */
    public List<ContentResponse> getAllContents() {
        return contentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific resource by ID.
     */
    public ContentResponse getContentById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found with id: " + id));
        return mapToResponse(content);
    }

    /**
     * Update an existing resource.
     */
    public ContentResponse updateContent(Long id, ContentRequest request) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found with id: " + id));

        content.setTitle(request.getTitle().trim());
        content.setAuthor(request.getAuthor().trim());
        content.setContentType(request.getContentType().trim());
        content.setCategory(request.getCategory().trim());
        content.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        content.setResourceUrl(request.getResourceUrl().trim());

        Content updated = contentRepository.save(content);
        return mapToResponse(updated);
    }

    /**
     * Delete resource by ID.
     */
    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("Content not found with id: " + id));
        contentRepository.delete(content);
    }

    /**
     * Search resources by keyword (matches title, author, or category).
     */
    public List<ContentResponse> searchContent(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllContents();
        }
        return contentRepository.searchByKeyword(keyword.trim()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ContentResponse mapToResponse(Content content) {
        return new ContentResponse(
                content.getId(),
                content.getTitle(),
                content.getAuthor(),
                content.getContentType(),
                content.getCategory(),
                content.getDescription(),
                content.getResourceUrl()
        );
    }
}

package com.dkp.content.controller;

import com.dkp.content.dto.ContentRequest;
import com.dkp.content.dto.ContentResponse;
import com.dkp.content.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Create a new digital academic resource.
     */
    @PostMapping
    public ResponseEntity<ContentResponse> createContent(@Valid @RequestBody ContentRequest request) {
        ContentResponse created = contentService.createContent(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Retrieve all academic resources.
     */
    @GetMapping
    public ResponseEntity<List<ContentResponse>> getAllContents() {
        return ResponseEntity.ok(contentService.getAllContents());
    }

    /**
     * Search academic resources by keyword (title, author, category).
     */
    @GetMapping("/search")
    public ResponseEntity<List<ContentResponse>> searchContent(@RequestParam(required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(contentService.searchContent(keyword));
    }

    /**
     * Retrieve a specific resource by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    /**
     * Update an existing resource by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> updateContent(@PathVariable Long id, @Valid @RequestBody ContentRequest request) {
        return ResponseEntity.ok(contentService.updateContent(id, request));
    }

    /**
     * Delete a resource by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.ok(Map.of(
                "message", "Content deleted successfully",
                "id", id
        ));
    }
}

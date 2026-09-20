package com.dkp.content;

import com.dkp.content.dto.ContentRequest;
import com.dkp.content.dto.ContentResponse;
import com.dkp.content.entity.Content;
import com.dkp.content.exception.ContentNotFoundException;
import com.dkp.content.repository.ContentRepository;
import com.dkp.content.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTests {

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    private Content sampleContent;

    @BeforeEach
    void setUp() {
        sampleContent = new Content(
                1L,
                "Java Programming",
                "Deepak",
                "BOOK",
                "PROGRAMMING",
                "Java programming learning resource",
                "https://example.com/java"
        );
    }

    @Test
    void testCreateContent() {
        ContentRequest request = new ContentRequest(
                "Java Programming",
                "Deepak",
                "BOOK",
                "PROGRAMMING",
                "Java programming learning resource",
                "https://example.com/java"
        );

        when(contentRepository.save(any(Content.class))).thenReturn(sampleContent);

        ContentResponse response = contentService.createContent(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Programming", response.getTitle());
        assertEquals("Deepak", response.getAuthor());
    }

    @Test
    void testGetContentById_Found() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(sampleContent));

        ContentResponse response = contentService.getContentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Programming", response.getTitle());
    }

    @Test
    void testGetContentById_NotFound() {
        when(contentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> contentService.getContentById(99L));
    }

    @Test
    void testUpdateContent() {
        ContentRequest updateRequest = new ContentRequest(
                "Advanced Java",
                "Deepak",
                "BOOK",
                "PROGRAMMING",
                "Updated description",
                "https://example.com/advanced-java"
        );

        when(contentRepository.findById(1L)).thenReturn(Optional.of(sampleContent));
        when(contentRepository.save(any(Content.class))).thenReturn(sampleContent);

        ContentResponse response = contentService.updateContent(1L, updateRequest);

        assertNotNull(response);
        verify(contentRepository, times(1)).save(sampleContent);
    }

    @Test
    void testDeleteContent() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(sampleContent));
        doNothing().when(contentRepository).delete(sampleContent);

        assertDoesNotThrow(() -> contentService.deleteContent(1L));
        verify(contentRepository, times(1)).delete(sampleContent);
    }

    @Test
    void testSearchContent() {
        when(contentRepository.searchByKeyword("java")).thenReturn(List.of(sampleContent));

        List<ContentResponse> results = contentService.searchContent("java");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
    }
}

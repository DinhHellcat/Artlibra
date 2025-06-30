package org.herukyatto.artlibra.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateTagRequest;
import org.herukyatto.artlibra.backend.dto.TagResponse;
import org.herukyatto.artlibra.backend.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được truy cập tất cả các API trong controller này
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody CreateTagRequest request) {
        return new ResponseEntity<>(tagService.createTag(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
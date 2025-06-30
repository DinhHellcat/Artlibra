package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CreateTagRequest;
import org.herukyatto.artlibra.backend.dto.TagResponse;

import java.util.List;

public interface TagService {
    TagResponse createTag(CreateTagRequest request);
    List<TagResponse> getAllTags();
    void deleteTag(Long tagId);
}
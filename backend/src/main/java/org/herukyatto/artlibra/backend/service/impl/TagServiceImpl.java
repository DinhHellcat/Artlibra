package org.herukyatto.artlibra.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateTagRequest;
import org.herukyatto.artlibra.backend.dto.TagResponse;
import org.herukyatto.artlibra.backend.entity.Tag;
import org.herukyatto.artlibra.backend.repository.TagRepository;
import org.herukyatto.artlibra.backend.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public TagResponse createTag(CreateTagRequest request) {
        if (tagRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Tag with name '" + request.getName() + "' already exists.");
        }
        Tag newTag = new Tag(request.getName());
        Tag savedTag = tagRepository.save(newTag);
        return new TagResponse(savedTag.getId(), savedTag.getName());
    }

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTag(Long tagId) {
        // Cần kiểm tra xem tag có đang được sử dụng không trước khi xóa,
        // nhưng để đơn giản, chúng ta sẽ xóa trực tiếp.
        // Sau này sẽ nâng cấp logic này.
        if (!tagRepository.existsById(tagId)) {
            throw new IllegalArgumentException("Tag not found with id: " + tagId);
        }
        tagRepository.deleteById(tagId);
    }
}
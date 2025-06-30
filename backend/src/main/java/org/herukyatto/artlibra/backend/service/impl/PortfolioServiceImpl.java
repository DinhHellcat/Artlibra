package org.herukyatto.artlibra.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreatePortfolioItemRequest;
import org.herukyatto.artlibra.backend.entity.PortfolioItem;
import org.herukyatto.artlibra.backend.entity.Tag;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.repository.PortfolioItemRepository;
import org.herukyatto.artlibra.backend.repository.TagRepository;
import org.herukyatto.artlibra.backend.service.CloudinaryService;
import org.herukyatto.artlibra.backend.service.PortfolioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final TagRepository tagRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public PortfolioItem createPortfolioItem(CreatePortfolioItemRequest request, MultipartFile file) {
        // 1. Lấy thông tin Họa sĩ đang đăng nhập
        User currentArtist = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Tải file ảnh lên Cloudinary và lấy URL
        String imageUrl = cloudinaryService.uploadFile(file);

        // 3. Tìm các đối tượng Tag từ danh sách tagId
        List<Tag> foundTags = tagRepository.findAllById(request.getTagIds());
        if (foundTags.size() != request.getTagIds().size()) {
            throw new IllegalArgumentException("One or more tags not found.");
        }

        // 4. Tạo đối tượng PortfolioItem mới
        PortfolioItem newItem = PortfolioItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .artist(currentArtist)
                .tags(new HashSet<>(foundTags))
                .build();

        // 5. Lưu vào CSDL và trả về
        return portfolioItemRepository.save(newItem);
    }
}
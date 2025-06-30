package org.herukyatto.artlibra.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "portfolio_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItem extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String imageUrl; // URL của ảnh tác phẩm trên Cloudinary

    // Mối quan hệ: Một tác phẩm thuộc về một Họa sĩ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    @JsonBackReference("user-portfolio-items")
    private User artist;

    // Mối quan hệ: Một tác phẩm có thể có nhiều tag
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "portfolio_item_tags",
            joinColumns = @JoinColumn(name = "portfolio_item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
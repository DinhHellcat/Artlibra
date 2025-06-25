package org.herukyatto.artlibra.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference; // <<== IMPORT MỚI

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "commissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commission extends AbstractEntity {

    @Column(nullable = false)
    private String title; // Tiêu đề của yêu cầu

    @Lob // Dùng cho các trường văn bản dài
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết công việc

    @Column(name = "min_budget", nullable = false)
    private BigDecimal minBudget; // Ngân sách tối thiểu

    @Column(name = "max_budget", nullable = false)
    private BigDecimal maxBudget; // Ngân sách tối đa

    private LocalDate deadline; // Hạn chót mong muốn

    @Enumerated(EnumType.STRING) // Lưu trạng thái dưới dạng chuỗi trong CSDL
    @Column(nullable = false)
    private CommissionStatus status; // Trạng thái hiện tại của yêu cầu

    // Mối quan hệ: Nhiều Commission có thể được tạo bởi một Client
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference("user-client-commissions") // <<== THÊM DÒNG NÀY
    private User client;

    // Mối quan hệ: Nhiều Commission có thể được thực hiện bởi một Artist
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artist_id")
    @JsonBackReference("user-artist-commissions") // <<== THÊM DÒNG NÀY
    private User artist;

    // Cột để lưu giá cuối cùng đã thỏa thuận
    @Column(name = "agreed_price")
    private BigDecimal agreedPrice;
}
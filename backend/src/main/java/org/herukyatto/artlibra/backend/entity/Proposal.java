package org.herukyatto.artlibra.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proposals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proposal extends AbstractEntity {

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String coverLetter; // Thư giới thiệu của họa sĩ

    @Column(name = "proposed_price", nullable = false)
    private BigDecimal proposedPrice; // Mức giá họa sĩ đề xuất

    @Column(name = "estimated_completion_date")
    private LocalDate estimatedCompletionDate; // Ngày hoàn thành dự kiến

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status; // Trạng thái của đơn ứng tuyển

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_id", nullable = false)
    @JsonBackReference("commission-proposals")
    private Commission commission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    @JsonBackReference("user-proposals")
    private User artist;
}
package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.entity.Proposal;
import org.herukyatto.artlibra.backend.dto.ProposalSummaryResponse;
import java.util.List;

public interface ProposalService {
    // Sửa lại phương thức createProposal
    Proposal createProposal(Long commissionId, CreateProposalRequest request);
    void deleteProposal(Long proposalId);
    List<ProposalSummaryResponse> getProposalsForCommission(Long commissionId);
}
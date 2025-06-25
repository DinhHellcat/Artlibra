package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.entity.Proposal;

public interface ProposalService {
    // Sửa lại phương thức createProposal
    Proposal createProposal(CreateProposalRequest request);
    void deleteProposal(Long proposalId);
}
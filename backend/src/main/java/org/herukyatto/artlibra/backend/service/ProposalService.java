package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.entity.Proposal;

public interface ProposalService {
    Proposal createProposal(Long commissionId, CreateProposalRequest request);
}
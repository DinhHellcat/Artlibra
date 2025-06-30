package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.entity.Proposal;
import org.herukyatto.artlibra.backend.dto.ProposalSummaryResponse;
import java.util.List;
import org.herukyatto.artlibra.backend.entity.Commission;

public interface ProposalService {
    Proposal createProposal(Long commissionId, CreateProposalRequest request);
    List<ProposalSummaryResponse> getProposalsForCommission(Long commissionId);
    void deleteProposal(Long commissionId, Long proposalId);
    Commission acceptProposal(Long commissionId, Long proposalId);
}
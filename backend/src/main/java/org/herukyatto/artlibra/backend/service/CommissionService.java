package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CreateCommissionRequest;
import org.herukyatto.artlibra.backend.entity.Commission;

public interface CommissionService {
    Commission createCommission(CreateCommissionRequest request);
}
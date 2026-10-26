package com.submeter.service;

import com.submeter.commonlib.dto.BillDTO;

import java.util.List;

public interface BillService {
    public List<BillDTO> getAll();
    public List<BillDTO> getAllPending();
}

package com.banco.app.service;

import com.banco.app.dto.request.CuentaRequestDTO;
import com.banco.app.dto.response.CuentaResponseDTO;

import java.util.List;

public interface CuentaService {
    List<CuentaResponseDTO> findAll();

    CuentaResponseDTO findById(Long id);

    List<CuentaResponseDTO> findByClienteId(Long clienteId);

    CuentaResponseDTO create(CuentaRequestDTO dto);

    CuentaResponseDTO update(Long id, CuentaRequestDTO dto);

    CuentaResponseDTO patch(Long id, CuentaRequestDTO dto);

    void delete(Long id);
}

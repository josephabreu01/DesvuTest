package com.banco.app.service;

import com.banco.app.dto.request.MovimientoRequestDTO;
import com.banco.app.dto.response.MovimientoResponseDTO;

import java.util.List;

public interface MovimientoService {
    List<MovimientoResponseDTO> findAll();

    MovimientoResponseDTO findById(Long id);

    List<MovimientoResponseDTO> findByCuentaId(Long cuentaId);

    MovimientoResponseDTO create(MovimientoRequestDTO dto);

    MovimientoResponseDTO update(Long id, MovimientoRequestDTO dto);

    MovimientoResponseDTO patch(Long id, MovimientoRequestDTO dto);

    void delete(Long id);
}

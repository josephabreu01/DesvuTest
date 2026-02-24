package com.banco.app.service;

import com.banco.app.dto.request.ClienteRequestDTO;
import com.banco.app.dto.response.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    List<ClienteResponseDTO> findAll();

    ClienteResponseDTO findById(Long id);

    ClienteResponseDTO findByClienteId(String clienteId);

    ClienteResponseDTO create(ClienteRequestDTO dto);

    ClienteResponseDTO update(Long id, ClienteRequestDTO dto);

    ClienteResponseDTO patch(Long id, ClienteRequestDTO dto);

    void delete(Long id);
}

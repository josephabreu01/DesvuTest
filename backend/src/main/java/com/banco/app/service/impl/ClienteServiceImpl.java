package com.banco.app.service.impl;

import com.banco.app.domain.Cliente;
import com.banco.app.dto.request.ClienteRequestDTO;
import com.banco.app.dto.response.ClienteResponseDTO;
import com.banco.app.exception.ResourceNotFoundException;
import com.banco.app.mapper.ClienteMapper;
import com.banco.app.repository.ClienteRepository;
import com.banco.app.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return clienteMapper.toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findByClienteId(String clienteId) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con clienteId: " + clienteId));
        return clienteMapper.toResponseDTO(cliente);
    }

    @Override
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        Cliente cliente = clienteMapper.toEntity(dto);
        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponseDTO update(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        clienteMapper.updateEntityFromDTO(cliente, dto);
        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponseDTO patch(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}


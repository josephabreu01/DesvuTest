package com.banco.app.service.impl;

import com.banco.app.domain.Cliente;
import com.banco.app.dto.request.ClienteRequestDTO;
import com.banco.app.dto.response.ClienteResponseDTO;
import com.banco.app.exception.ResourceNotFoundException;
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

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findByClienteId(String clienteId) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con clienteId: " + clienteId));
        return toResponseDTO(cliente);
    }

    @Override
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        Cliente cliente = toEntity(dto);
        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponseDTO update(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        updateEntityFromDTO(cliente, dto);
        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponseDTO patch(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(cliente.getClienteId())
                .estado(cliente.getEstado())
                .build();
    }

    private Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        updateEntityFromDTO(cliente, dto);
        return cliente;
    }

    private void updateEntityFromDTO(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNombre(dto.nombre());
        cliente.setApellido(dto.apellido());
        cliente.setDireccion(dto.direccion());
        cliente.setTelefono(dto.telefono());
        if (dto.clienteId() != null && !dto.clienteId().trim().isEmpty()) {
            cliente.setClienteId(dto.clienteId());
        }
        if (dto.contrasena() != null && !dto.contrasena().trim().isEmpty()) {
            cliente.setContrasena(dto.contrasena());
        }
        cliente.setEstado(dto.estado());
    }
}

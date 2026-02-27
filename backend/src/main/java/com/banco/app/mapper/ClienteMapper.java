package com.banco.app.mapper;

import com.banco.app.domain.Cliente;
import com.banco.app.dto.request.ClienteRequestDTO;
import com.banco.app.dto.response.ClienteResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
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

    public Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        updateEntityFromDTO(cliente, dto);
        return cliente;
    }

    public void updateEntityFromDTO(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNombre(dto.nombre());
        cliente.setApellido(dto.apellido());
        cliente.setDireccion(dto.direccion());
        cliente.setTelefono(dto.telefono());
        cliente.setContrasena(dto.contrasena());
        if (dto.estado() != null) {
            cliente.setEstado(dto.estado());
        }
    }
}

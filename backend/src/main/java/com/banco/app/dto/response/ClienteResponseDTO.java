package com.banco.app.dto.response;

import lombok.*;

@Builder
public record ClienteResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String direccion,
        String telefono,
        String clienteId,
        Boolean estado) {
}

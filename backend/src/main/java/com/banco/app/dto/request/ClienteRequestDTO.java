package com.banco.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
public record ClienteRequestDTO(
        @NotNull @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotNull @NotBlank(message = "El apellido es obligatorio") String apellido,
        @NotNull @NotBlank(message = "La dirección es obligatoria") String direccion,
        @NotNull @NotBlank(message = "El teléfono es obligatorio") String telefono,
        String clienteId,
        String contrasena,

        Boolean estado) {
}

package com.banco.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;


@Builder
public record MovimientoRequestDTO(
        @NotBlank(message = "El tipo de movimiento es obligatorio")
         String tipoMovimiento,

        @NotNull(message = "El valor es obligatorio")
         BigDecimal valor,

        @NotNull(message = "El ID de cuenta es obligatorio")
         Long cuentaId) {


}

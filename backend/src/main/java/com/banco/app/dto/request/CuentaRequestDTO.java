package com.banco.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
public record CuentaRequestDTO(
        @NotNull @NotBlank(message = "El tipo de cuenta es obligatorio") String tipoCuenta,

        @NotNull(message = "El saldo inicial es obligatorio") @DecimalMin(value = "0.0", message = "El saldo inicial no puede ser negativo") BigDecimal saldoInicial,

        Boolean estado,

        @NotNull(message = "El clienteId es obligatorio") Long clienteId) {

}

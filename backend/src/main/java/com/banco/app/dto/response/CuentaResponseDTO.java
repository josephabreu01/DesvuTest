package com.banco.app.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record CuentaResponseDTO(
        Long id,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estado,
        Long clienteId,
        String clienteNombre) {

}

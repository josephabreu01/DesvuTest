package com.banco.app.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record MovimientoResponseDTO(
        Long id,
        LocalDate fecha,
        String tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldo,
        Long cuentaId,
        String numeroCuenta,
        String tipoCuenta,
        Boolean estadoCuenta) {

}

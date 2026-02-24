package com.banco.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteDTO(
        String clienteNombre,
        String clienteApellido,
        String clienteId,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estadoCuenta,
        List<MovimientoDetalleDTO> movimientos) {
    public record MovimientoDetalleDTO(
            LocalDate fecha,
            String tipoMovimiento,
            BigDecimal valor,
            BigDecimal saldo) {
    }
}

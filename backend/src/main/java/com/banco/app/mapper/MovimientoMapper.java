package com.banco.app.mapper;

import com.banco.app.domain.Movimiento;
import com.banco.app.dto.response.MovimientoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {

    public MovimientoResponseDTO toResponseDTO(Movimiento movimiento) {
    return MovimientoResponseDTO.builder()
        .id(movimiento.getId())
        .cuentaId(movimiento.getCuenta().getId())
        .tipoMovimiento(movimiento.getTipoMovimiento())
        .valor(movimiento.getValor())
        .saldo(movimiento.getSaldo())
        .fecha(movimiento.getFecha())
        .numeroCuenta(movimiento.getCuenta() != null ? movimiento.getCuenta().getNumeroCuenta() : "")
        .tipoCuenta(movimiento.getCuenta() != null ? movimiento.getCuenta().getTipoCuenta() : "")
        .estadoCuenta(movimiento.getCuenta() != null ? movimiento.getCuenta().getEstado() : Boolean.FALSE)
        .build();
    }
}

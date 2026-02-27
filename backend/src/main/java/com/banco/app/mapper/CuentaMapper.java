package com.banco.app.mapper;

import com.banco.app.domain.Cuenta;
import com.banco.app.dto.request.CuentaRequestDTO;
import com.banco.app.dto.response.CuentaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {

    public CuentaResponseDTO toResponseDTO(Cuenta cuenta) {
        return CuentaResponseDTO.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getCliente().getId())
                .clienteNombre(cuenta.getCliente().getNombre())
                .build();
    }

    public Cuenta toEntity(CuentaRequestDTO dto) {
        Cuenta cuenta = new Cuenta();
        updateEntityFromDTO(cuenta, dto);
        return cuenta;
    }

    public void updateEntityFromDTO(Cuenta cuenta, CuentaRequestDTO dto) {
        cuenta.setTipoCuenta(dto.tipoCuenta());
        cuenta.setSaldoInicial(dto.saldoInicial());
        cuenta.setEstado(dto.estado() != null ? dto.estado() : true);
    }
}

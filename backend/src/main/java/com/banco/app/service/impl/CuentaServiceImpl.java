package com.banco.app.service.impl;

import com.banco.app.domain.Cliente;
import com.banco.app.domain.Cuenta;
import com.banco.app.dto.request.CuentaRequestDTO;
import com.banco.app.dto.response.CuentaResponseDTO;
import com.banco.app.exception.ResourceNotFoundException;
import com.banco.app.repository.ClienteRepository;
import com.banco.app.repository.CuentaRepository;
import com.banco.app.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> findAll() {
        return cuentaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponseDTO findById(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));
        return toResponseDTO(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> findByClienteId(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CuentaResponseDTO create(CuentaRequestDTO dto) {
        String numeroCuenta;
        do {
            numeroCuenta = String.format("%010d", (long) (Math.random() * 10000000000L));
        } while (cuentaRepository.existsByNumeroCuenta(numeroCuenta));
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cliente no encontrado con id: " + dto.clienteId()));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setTipoCuenta(dto.tipoCuenta());
        cuenta.setSaldoInicial(dto.saldoInicial());
        cuenta.setEstado(dto.estado() != null ? dto.estado() : true);
        cuenta.setCliente(cliente);

        return toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public CuentaResponseDTO update(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cliente no encontrado con id: " + dto.clienteId()));

        cuenta.setTipoCuenta(dto.tipoCuenta());
        cuenta.setSaldoInicial(dto.saldoInicial());
        cuenta.setEstado(dto.estado() != null ? dto.estado() : true);
        cuenta.setCliente(cliente);

        return toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public CuentaResponseDTO patch(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));

        return toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public void delete(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cuenta no encontrada con id: " + id);
        }
        cuentaRepository.deleteById(id);
    }

    private CuentaResponseDTO toResponseDTO(Cuenta cuenta) {
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
}

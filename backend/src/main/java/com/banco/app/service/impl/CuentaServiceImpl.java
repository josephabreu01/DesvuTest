package com.banco.app.service.impl;

import com.banco.app.domain.Cliente;
import com.banco.app.domain.Cuenta;
import com.banco.app.dto.request.CuentaRequestDTO;
import com.banco.app.dto.response.CuentaResponseDTO;
import com.banco.app.exception.ResourceNotFoundException;
import com.banco.app.mapper.CuentaMapper;
import com.banco.app.repository.ClienteRepository;
import com.banco.app.repository.CuentaRepository;
import com.banco.app.service.CuentaService;
import com.banco.app.service.util.AccountNumberGenerator;
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
    private final CuentaMapper cuentaMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> findAll() {
        return cuentaRepository.findAll().stream()
                .map(cuentaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponseDTO findById(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));
        return cuentaMapper.toResponseDTO(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> findByClienteId(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(cuentaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CuentaResponseDTO create(CuentaRequestDTO dto) {
        String numeroCuenta = accountNumberGenerator.generateUniqueAccountNumber();
        
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cliente no encontrado con id: " + dto.clienteId()));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(numeroCuenta);
        cuentaMapper.updateEntityFromDTO(cuenta, dto);
        cuenta.setCliente(cliente);

        return cuentaMapper.toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public CuentaResponseDTO update(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cliente no encontrado con id: " + dto.clienteId()));

        cuentaMapper.updateEntityFromDTO(cuenta, dto);
        cuenta.setCliente(cliente);

        return cuentaMapper.toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public CuentaResponseDTO patch(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));

        return cuentaMapper.toResponseDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public void delete(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cuenta no encontrada con id: " + id);
        }
        cuentaRepository.deleteById(id);
    }
}

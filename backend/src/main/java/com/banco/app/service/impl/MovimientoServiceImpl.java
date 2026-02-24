package com.banco.app.service.impl;

import com.banco.app.domain.Cuenta;
import com.banco.app.domain.Movimiento;
import com.banco.app.dto.request.MovimientoRequestDTO;
import com.banco.app.dto.response.MovimientoResponseDTO;
import com.banco.app.exception.BusinessException;
import com.banco.app.exception.ResourceNotFoundException;
import com.banco.app.repository.CuentaRepository;
import com.banco.app.repository.MovimientoRepository;
import com.banco.app.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> findAll() {
        return movimientoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponseDTO findById(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
        return toResponseDTO(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> findByCuentaId(Long cuentaId) {
        return movimientoRepository.findByCuentaId(cuentaId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MovimientoResponseDTO create(MovimientoRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(dto.cuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + dto.cuentaId()));

        if (!cuenta.getEstado()) {
            throw new BusinessException("La cuenta está inactiva");
        }

        BigDecimal valor = dto.valor();
        String tipo = dto.tipoMovimiento().toUpperCase();
        BigDecimal limiteDiario = new BigDecimal("1000");

        if (tipo.equals("RETIRO")) {
            BigDecimal valorAbsoluto = valor.abs();
            BigDecimal totalHoy = movimientoRepository.sumRetirosDia(cuenta.getCliente().getClienteId(),
                    LocalDate.now());

            if (totalHoy.add(valorAbsoluto).compareTo(limiteDiario) > 0) {
                throw new BusinessException("Cupo diario Excedido");
            }

            // Withdrawals must be negative
            if (valor.compareTo(BigDecimal.ZERO) > 0) {
                valor = valor.negate();
            }
        } else if (tipo.equals("DEPOSITO")) {
            // Deposits must be positive
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                valor = valor.abs();
            }
        }

        BigDecimal nuevoSaldo = cuenta.getSaldoInicial().add(valor);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Saldo no disponible. Saldo actual: " + cuenta.getSaldoInicial());
        }

        // Save movement
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDate.now());
        movimiento.setTipoMovimiento(dto.tipoMovimiento());
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        // Update account balance
        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return toResponseDTO(movimientoRepository.save(movimiento));
    }

    @Override
    public MovimientoResponseDTO update(Long id, MovimientoRequestDTO dto) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

        // 1. Reverse previous movement value
        Cuenta cuenta = movimiento.getCuenta();
        BigDecimal saldoSinMovimiento = cuenta.getSaldoInicial().subtract(movimiento.getValor());

        // 2. Calculate new value based on type
        BigDecimal valorActual = dto.valor();
        String tipo = dto.tipoMovimiento().toUpperCase();
        BigDecimal nuevoValor = getValor(valorActual, tipo);

        // 3. Verify new balance
        BigDecimal nuevoSaldo = saldoSinMovimiento.add(nuevoValor);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Saldo no disponible. Saldo tras ajuste sería negativo: " + nuevoSaldo);
        }

        // 4. Persistence
        movimiento.setTipoMovimiento(dto.tipoMovimiento());
        movimiento.setValor(nuevoValor);
        movimiento.setSaldo(nuevoSaldo);

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return toResponseDTO(movimientoRepository.save(movimiento));
    }

    private BigDecimal getValor(BigDecimal valorActual, String tipo) {
        BigDecimal nuevoValor = valorActual;
        if (tipo.equals("RETIRO")) {
            if (nuevoValor.compareTo(BigDecimal.ZERO) > 0)
                nuevoValor = valorActual.negate();
        } else if (tipo.equals("DEPOSITO")) {
            if (nuevoValor.compareTo(BigDecimal.ZERO) < 0)
                nuevoValor = valorActual.abs();
        }
        return nuevoValor;
    }

    @Override
    public MovimientoResponseDTO patch(Long id, MovimientoRequestDTO dto) {
        return update(id, dto);
    }

    @Override
    public void delete(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

        // Reverse the movement before deleting
        Cuenta cuenta = movimiento.getCuenta();
        cuenta.setSaldoInicial(cuenta.getSaldoInicial().subtract(movimiento.getValor()));
        cuentaRepository.save(cuenta);

        movimientoRepository.deleteById(id);
    }

    private MovimientoResponseDTO toResponseDTO(Movimiento movimiento) {
        return MovimientoResponseDTO.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .cuentaId(movimiento.getCuenta().getId())
                .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
                .tipoCuenta(movimiento.getCuenta().getTipoCuenta())
                .estadoCuenta(movimiento.getCuenta().getEstado())
                .build();
    }
}

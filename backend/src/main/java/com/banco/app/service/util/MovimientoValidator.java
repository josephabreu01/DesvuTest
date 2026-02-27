package com.banco.app.service.util;

import com.banco.app.domain.Cuenta;
import com.banco.app.exception.BusinessException;
import com.banco.app.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MovimientoValidator {

    private static final BigDecimal LIMITE_DIARIO_RETIRO = new BigDecimal("1000");
    private final MovimientoRepository movimientoRepository;

    public void validateWithdrawal(Cuenta cuenta, BigDecimal valor) {
        if (!cuenta.getEstado()) {
            throw new BusinessException("La cuenta está inactiva");
        }

        BigDecimal valorAbsoluto = valor.abs();
        BigDecimal totalHoy = movimientoRepository.sumRetirosDia(
                cuenta.getCliente().getClienteId(),
                LocalDate.now()
        );

        if (totalHoy.add(valorAbsoluto).compareTo(LIMITE_DIARIO_RETIRO) > 0) {
            throw new BusinessException("Cupo diario Excedido");
        }
    }

    public void validateDeposit(Cuenta cuenta) {
        if (!cuenta.getEstado()) {
            throw new BusinessException("La cuenta está inactiva");
        }
    }

    public BigDecimal normalizeWithdrawalAmount(BigDecimal valor) {
        // Withdrawals must be negative
        return valor.compareTo(BigDecimal.ZERO) > 0 ? valor.negate() : valor;
    }

    public BigDecimal normalizeDepositAmount(BigDecimal valor) {
        // Deposits must be positive
        return valor.compareTo(BigDecimal.ZERO) < 0 ? valor.negate() : valor;
    }
}

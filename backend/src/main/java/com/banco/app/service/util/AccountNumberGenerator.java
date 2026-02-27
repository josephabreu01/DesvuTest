package com.banco.app.service.util;

import com.banco.app.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final CuentaRepository cuentaRepository;

    public String generateUniqueAccountNumber() {
        String numeroCuenta;
        do {
            numeroCuenta = String.format("%010d", (long) (Math.random() * 10000000000L));
        } while (cuentaRepository.existsByNumeroCuenta(numeroCuenta));
        return numeroCuenta;
    }
}

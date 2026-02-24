package com.banco.app.service;

import com.banco.app.domain.Cuenta;
import com.banco.app.domain.Movimiento;
import com.banco.app.dto.request.MovimientoRequestDTO;
import com.banco.app.dto.response.MovimientoResponseDTO;
import com.banco.app.exception.BusinessException;
import com.banco.app.repository.CuentaRepository;
import com.banco.app.repository.MovimientoRepository;
import com.banco.app.service.impl.MovimientoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    private Cuenta cuenta;
    private Movimiento movimiento;
    private MovimientoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("4759874563");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(new BigDecimal("1000.00"));
        cuenta.setEstado(true);

        movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setFecha(LocalDate.now());
        movimiento.setTipoMovimiento("DEPOSITO");
        movimiento.setValor(new BigDecimal("500.00"));
        movimiento.setSaldo(new BigDecimal("1500.00"));
        movimiento.setCuenta(cuenta);

        requestDTO = new MovimientoRequestDTO(
                "DEPOSITO",
                new BigDecimal("500.00"),
                1L);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(movimientoRepository.findAll()).thenReturn(List.of(movimiento));

        List<MovimientoResponseDTO> response = movimientoService.findAll();

        assertFalse(response.isEmpty());
        assertEquals("DEPOSITO", response.get(0).tipoMovimiento());
        verify(movimientoRepository, times(1)).findAll();
    }

    @Test
    void create_ShouldReturnCreatedMovimiento_WhenValidDeposit() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        MovimientoResponseDTO response = movimientoService.create(requestDTO);

        assertNotNull(response);
        assertEquals("DEPOSITO", response.tipoMovimiento());
        assertEquals(new BigDecimal("1500.00"), cuenta.getSaldoInicial()); // Balance updated
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }

    @Test
    void create_ShouldThrowException_WhenAccountInactive() {
        cuenta.setEstado(false);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(BusinessException.class, () -> movimientoService.create(requestDTO));
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void create_ShouldThrowException_WhenInsufficientBalance() {
        // Current balance is 1000.00 (set in setUp)
        // Trying to withdraw 2000.00
        MovimientoRequestDTO retiroRequest = new MovimientoRequestDTO("RETIRO", new BigDecimal("2000.00"), 1L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(BusinessException.class, () -> movimientoService.create(retiroRequest));
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void update_ShouldUpdateBalanceAndReturnMovimiento() {
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        MovimientoRequestDTO updatedRequest = new MovimientoRequestDTO("DEPOSITO", new BigDecimal("700.00"), 1L);

        // Simular que el saldo de cuenta actual tiene el depósito original sumado (1000
        // base + 500 = 1500)
        cuenta.setSaldoInicial(new BigDecimal("1500.00"));

        MovimientoResponseDTO response = movimientoService.update(1L, updatedRequest);

        assertNotNull(response);
        // Original balance without movement = 1500 - 500 = 1000
        // New calculation = 1000 + 700 = 1700
        assertEquals(new BigDecimal("1700.00"), cuenta.getSaldoInicial());
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void delete_ShouldReverseBalanceAndDelete() {
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));
        cuenta.setSaldoInicial(new BigDecimal("1500.00")); // Includes the 500 deposit

        doNothing().when(movimientoRepository).deleteById(1L);

        movimientoService.delete(1L);

        assertEquals(new BigDecimal("1000.00"), cuenta.getSaldoInicial());
        verify(cuentaRepository, times(1)).save(cuenta);
        verify(movimientoRepository, times(1)).deleteById(1L);
    }

    @Test
    void create_ShouldSubtractBalance_WhenRetiro() {
        // Current balance is 1000.00
        MovimientoRequestDTO retiroRequest = new MovimientoRequestDTO("RETIRO", new BigDecimal("400.00"), 1L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        // Mock save returning a movement with value -400
        Movimiento savedMov = new Movimiento();
        savedMov.setValor(new BigDecimal("-400.00"));
        savedMov.setSaldo(new BigDecimal("600.00"));
        savedMov.setTipoMovimiento("RETIRO");
        savedMov.setCuenta(cuenta);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(savedMov);

        MovimientoResponseDTO response = movimientoService.create(retiroRequest);

        assertEquals(new BigDecimal("-400.00"), response.valor());
        assertEquals(new BigDecimal("600.00"), cuenta.getSaldoInicial());
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }
}

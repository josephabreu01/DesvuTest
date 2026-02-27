package com.banco.app.service;

import com.banco.app.domain.Cliente;
import com.banco.app.domain.Cuenta;
import com.banco.app.dto.request.CuentaRequestDTO;
import com.banco.app.dto.response.CuentaResponseDTO;
import com.banco.app.mapper.CuentaMapper;
import com.banco.app.repository.ClienteRepository;
import com.banco.app.repository.CuentaRepository;
import com.banco.app.service.impl.CuentaServiceImpl;
import com.banco.app.service.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    private CuentaMapper cuentaMapper = new CuentaMapper();

    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    private Cuenta cuenta;
    private Cliente cliente;
    private CuentaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        accountNumberGenerator = new AccountNumberGenerator(cuentaRepository);
        cuentaService = new CuentaServiceImpl(cuentaRepository, clienteRepository, cuentaMapper, accountNumberGenerator);
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Perez");

        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("4759874563");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(new BigDecimal("500.00"));
        cuenta.setEstado(true);
        cuenta.setCliente(cliente);

        requestDTO = new CuentaRequestDTO(
                "Ahorro",
                new BigDecimal("500.00"),
                true,
                1L);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(cuentaRepository.findAll()).thenReturn(List.of(cuenta));

        List<CuentaResponseDTO> response = cuentaService.findAll();

        assertFalse(response.isEmpty());
        assertEquals("4759874563", response.get(0).numeroCuenta());
        verify(cuentaRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnCuenta() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        CuentaResponseDTO response = cuentaService.findById(1L);

        assertNotNull(response);
        assertEquals("4759874563", response.numeroCuenta());
        verify(cuentaRepository, times(1)).findById(1L);
    }

    @Test
    void create_ShouldReturnCreatedCuenta() {
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        CuentaResponseDTO response = cuentaService.create(requestDTO);

        assertNotNull(response);
        assertEquals("4759874563", response.numeroCuenta());
        assertEquals("Juan Perez", response.clienteNombre());
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void delete_ShouldCallDelete() {
        when(cuentaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cuentaRepository).deleteById(1L);

        assertDoesNotThrow(() -> cuentaService.delete(1L));
        verify(cuentaRepository, times(1)).deleteById(1L);
    }
}

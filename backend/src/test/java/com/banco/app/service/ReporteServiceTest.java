package com.banco.app.service;

import com.banco.app.domain.Cliente;
import com.banco.app.domain.Cuenta;
import com.banco.app.domain.Movimiento;
import com.banco.app.dto.response.ReporteDTO;
import com.banco.app.dto.request.ReporteRequestDTO;
import com.banco.app.exception.ResourceNotFoundException;
import com.banco.app.repository.ClienteRepository;
import com.banco.app.repository.MovimientoRepository;
import com.banco.app.service.impl.ReporteServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private Cliente cliente;
    private Cuenta cuenta;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setClienteId("juan123");
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");

        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("4759874563");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(new BigDecimal("500.00"));
        cuenta.setEstado(true);

        // No changes needed, ReporteServiceTest doesn't instantiate ReporteDTO itself
        cliente.setCuentas(List.of(cuenta));

        movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setFecha(LocalDate.now());
        movimiento.setTipoMovimiento("DEPOSITO");
        movimiento.setValor(new BigDecimal("500.00"));
        movimiento.setSaldo(new BigDecimal("1000.00"));
        movimiento.setCuenta(cuenta);
    }

    @Test
    void generateReport_ShouldReturnReportData_WhenMovementsExist() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        when(clienteRepository.findByClienteId("juan123")).thenReturn(Optional.of(cliente));
        when(movimientoRepository.findByCuentaClienteClienteIdAndFechaBetween("juan123", start, end))
                .thenReturn(List.of(movimiento));

        ReporteRequestDTO request = new ReporteRequestDTO("juan123", null, start, end);
        List<ReporteDTO> reportes = reporteService.generateReport(request);

        assertFalse(reportes.isEmpty());
        assertEquals(1, reportes.size());

        ReporteDTO reporte = reportes.get(0);
        assertEquals("Juan", reporte.clienteNombre());
        assertEquals("Perez", reporte.clienteApellido());
        assertEquals("4759874563", reporte.numeroCuenta());
        assertEquals(1, reporte.movimientos().size());
        assertEquals("DEPOSITO", reporte.movimientos().get(0).tipoMovimiento());
    }

    @Test
    void generateReport_ShouldReturnBasicReportData_WhenNoMovementsExist() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        when(clienteRepository.findByClienteId("juan123")).thenReturn(Optional.of(cliente));
        when(movimientoRepository.findByCuentaClienteClienteIdAndFechaBetween("juan123", start, end))
                .thenReturn(List.of());

        ReporteRequestDTO request = new ReporteRequestDTO("juan123", null, start, end);
        List<ReporteDTO> reportes = reporteService.generateReport(request);

        assertFalse(reportes.isEmpty());
        assertEquals(1, reportes.size());

        ReporteDTO reporte = reportes.get(0);
        assertEquals("Juan", reporte.clienteNombre());
        assertEquals("Perez", reporte.clienteApellido());
        assertEquals("4759874563", reporte.numeroCuenta());
        assertTrue(reporte.movimientos().isEmpty());
    }

    @Test
    void generateReport_ShouldThrowException_WhenClientNotFound() {
        when(clienteRepository.findByClienteId("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reporteService
                        .generateReport(new ReporteRequestDTO("unknown", null, LocalDate.now(), LocalDate.now())));
    }
}

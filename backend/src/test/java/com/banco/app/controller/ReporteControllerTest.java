package com.banco.app.controller;

import com.banco.app.dto.response.ReporteDTO;
import com.banco.app.dto.request.ReporteRequestDTO;
import com.banco.app.exception.GlobalExceptionHandler;
import com.banco.app.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
@Import(GlobalExceptionHandler.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    private ReporteDTO reporteResponse;

    @BeforeEach
    void setUp() {
        ReporteDTO.MovimientoDetalleDTO mov = new ReporteDTO.MovimientoDetalleDTO(
                LocalDate.now(),
                "DEPOSITO",
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"));

        reporteResponse = new ReporteDTO(
                "Juan",
                "Perez",
                "juan123",
                "4759874563",
                "Ahorro",
                new BigDecimal("500.00"),
                true,
                List.of(mov));
    }

    @Test
    void generarReporte_ShouldReturnReporteData() throws Exception {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();
        String clienteId = "juan123";

        ReporteRequestDTO request = new ReporteRequestDTO(clienteId, null, start, end);

        when(reporteService.generateReport(request))
                .thenReturn(List.of(reporteResponse));

        mockMvc.perform(get("/reportes")
                .param("clienteId", clienteId)
                .param("fechaInicio", start.toString())
                .param("fechaFin", end.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].clienteNombre").value("Juan"))
                .andExpect(jsonPath("$[0].clienteApellido").value("Perez"))
                .andExpect(jsonPath("$[0].numeroCuenta").value("4759874563"))
                .andExpect(jsonPath("$[0].movimientos[0].tipoMovimiento").value("DEPOSITO"));

        verify(reporteService, times(1)).generateReport(any(ReporteRequestDTO.class));
    }
}

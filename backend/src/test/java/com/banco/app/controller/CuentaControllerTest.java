package com.banco.app.controller;

import com.banco.app.dto.request.CuentaRequestDTO;
import com.banco.app.dto.response.CuentaResponseDTO;
import com.banco.app.exception.GlobalExceptionHandler;
import com.banco.app.service.CuentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
@Import(GlobalExceptionHandler.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuentaService cuentaService;

    @Autowired
    private ObjectMapper objectMapper;

    private CuentaResponseDTO cuentaResponse;
    private CuentaRequestDTO cuentaRequest;

    @BeforeEach
    void setUp() {
        cuentaResponse = new CuentaResponseDTO(
                1L,
                "4759874563",
                "Ahorro",
                new BigDecimal("500.00"),
                true,
                1L,
                "Juan Perez");

        cuentaRequest = new CuentaRequestDTO(
                "Ahorro",
                new BigDecimal("500.00"),
                true,
                1L);
    }

    @Test
    void findAll_ShouldReturnListOfCuentas() throws Exception {
        when(cuentaService.findAll()).thenReturn(List.of(cuentaResponse));

        mockMvc.perform(get("/cuentas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].numeroCuenta").value("4759874563"))
                .andExpect(jsonPath("$[0].saldoInicial").value(500.00));

        verify(cuentaService, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnCuenta() throws Exception {
        when(cuentaService.findById(1L)).thenReturn(cuentaResponse);

        mockMvc.perform(get("/cuentas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroCuenta").value("4759874563"));
    }

    @Test
    void create_ShouldReturnCreatedCuenta() throws Exception {
        when(cuentaService.create(any(CuentaRequestDTO.class))).thenReturn(cuentaResponse);

        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("4759874563"))
                .andExpect(jsonPath("$.clienteNombre").value("Juan Perez"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        CuentaRequestDTO invalidRequest = new CuentaRequestDTO(
                "Ahorro",
                new BigDecimal("-100.00"), // invalid negative
                true,
                1L);

        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturnUpdatedCuenta() throws Exception {
        CuentaResponseDTO updated = new CuentaResponseDTO(
                1L,
                "4759874563",
                "Corriente",
                new BigDecimal("1000.00"),
                true,
                1L,
                "Juan Perez");

        when(cuentaService.update(eq(1L), any(CuentaRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/cuentas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoCuenta").value("Corriente"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(cuentaService).delete(1L);

        mockMvc.perform(delete("/cuentas/1"))
                .andExpect(status().isNoContent());

        verify(cuentaService, times(1)).delete(1L);
    }
}

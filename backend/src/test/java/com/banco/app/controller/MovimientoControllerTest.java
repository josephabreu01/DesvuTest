package com.banco.app.controller;

import com.banco.app.dto.request.MovimientoRequestDTO;
import com.banco.app.dto.response.MovimientoResponseDTO;
import com.banco.app.exception.BusinessException;
import com.banco.app.exception.GlobalExceptionHandler;
import com.banco.app.service.MovimientoService;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovimientoController.class)
@Import(GlobalExceptionHandler.class)
class MovimientoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MovimientoService movimientoService;

        @Autowired
        private ObjectMapper objectMapper;

        private MovimientoResponseDTO movimientoResponse;

        @BeforeEach
        void setUp() {
                movimientoResponse = new MovimientoResponseDTO(
                                1L,
                                LocalDate.now(),
                                "DEPOSITO",
                                new BigDecimal("500.00"),
                                new BigDecimal("1500.00"),
                                1L,
                                "4759874563",
                                "AHORRO",
                                true);
        }

        @Test
        void findAll_ShouldReturnListOfMovimientos() throws Exception {
                when(movimientoService.findAll()).thenReturn(List.of(movimientoResponse));

                mockMvc.perform(get("/movimientos")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].tipoMovimiento").value("DEPOSITO"))
                                .andExpect(jsonPath("$[0].valor").value(500.00))
                                .andExpect(jsonPath("$[0].saldo").value(1500.00));

                verify(movimientoService, times(1)).findAll();
        }

        @Test
        void create_ShouldReturnCreated_WhenValidRequest() throws Exception {
                MovimientoRequestDTO request = new MovimientoRequestDTO(
                                "DEPOSITO",
                                new BigDecimal("500.00"),
                                1L);

                when(movimientoService.create(any(MovimientoRequestDTO.class))).thenReturn(movimientoResponse);

                mockMvc.perform(post("/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tipoMovimiento").value("DEPOSITO"))
                                .andExpect(jsonPath("$.saldo").value(1500.00));
        }

        @Test
        void create_ShouldReturnBadRequest_WhenInsufficientBalance() throws Exception {
                MovimientoRequestDTO request = new MovimientoRequestDTO(
                                "RETIRO",
                                new BigDecimal("-9999.00"),
                                1L);

                when(movimientoService.create(any(MovimientoRequestDTO.class)))
                                .thenThrow(new BusinessException("Saldo no disponible. Saldo actual: 100.00"));

                mockMvc.perform(post("/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Saldo no disponible. Saldo actual: 100.00"));
        }

        @Test
        void delete_ShouldReturnNoContent() throws Exception {
                doNothing().when(movimientoService).delete(1L);

                mockMvc.perform(delete("/movimientos/1"))
                                .andExpect(status().isNoContent());

                verify(movimientoService, times(1)).delete(1L);
        }
}

package com.banco.app.controller;

import com.banco.app.dto.request.ClienteRequestDTO;
import com.banco.app.dto.response.ClienteResponseDTO;
import com.banco.app.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ClienteService clienteService;

        @Autowired
        private ObjectMapper objectMapper;

        private ClienteResponseDTO clienteResponse;
        private ClienteRequestDTO clienteRequest;

        @BeforeEach
        void setUp() {
                clienteResponse = new ClienteResponseDTO(
                                1L,
                                "Juan",
                                "Perez",
                                "Calle Primera 123",
                                "0999123456",
                                "juan123",
                                true);

                clienteRequest = new ClienteRequestDTO(
                                "Juan",
                                "Perez",
                                "Calle Primera 123",
                                "0999123456",
                                "juan123",
                                "password123",
                                true);
        }

        @Test
        void findAll_ShouldReturnListOfClientes() throws Exception {
                when(clienteService.findAll()).thenReturn(List.of(clienteResponse));

                mockMvc.perform(get("/clientes")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                                .andExpect(jsonPath("$[0].apellido").value("Perez"))
                                .andExpect(jsonPath("$[0].clienteId").value("juan123"));

                verify(clienteService, times(1)).findAll();
        }

        @Test
        void findById_ShouldReturnCliente_WhenExists() throws Exception {
                when(clienteService.findById(1L)).thenReturn(clienteResponse);

                mockMvc.perform(get("/clientes/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nombre").value("Juan"))
                                .andExpect(jsonPath("$.apellido").value("Perez"));
        }

        @Test
        void create_ShouldReturnCreatedCliente_WhenValidRequest() throws Exception {
                when(clienteService.create(any(ClienteRequestDTO.class))).thenReturn(clienteResponse);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.clienteId").value("juan123"))
                                .andExpect(jsonPath("$.nombre").value("Juan"))
                                .andExpect(jsonPath("$.apellido").value("Perez"));

                verify(clienteService, times(1)).create(any(ClienteRequestDTO.class));
        }

        @Test
        void create_ShouldReturnBadRequest_WhenMissingRequiredField() throws Exception {
                ClienteRequestDTO invalidRequest = new ClienteRequestDTO(
                                "", // invalid: blank
                                "Perez",
                                "Calle 1",
                                "09999",
                                "juan123",
                                "pass",
                                true);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void update_ShouldReturnUpdatedCliente() throws Exception {
                ClienteResponseDTO updated = new ClienteResponseDTO(
                                1L, "Juan", "Actualizado", "Calle Nueva", "0999123456",
                                "juan123", true);

                when(clienteService.update(eq(1L), any(ClienteRequestDTO.class))).thenReturn(updated);

                mockMvc.perform(put("/clientes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Juan"))
                                .andExpect(jsonPath("$.apellido").value("Actualizado"));
        }

        @Test
        void delete_ShouldReturnNoContent() throws Exception {
                doNothing().when(clienteService).delete(1L);

                mockMvc.perform(delete("/clientes/1"))
                                .andExpect(status().isNoContent());

                verify(clienteService, times(1)).delete(1L);
        }
}

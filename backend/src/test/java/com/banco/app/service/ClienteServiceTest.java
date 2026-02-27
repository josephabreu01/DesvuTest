package com.banco.app.service;

import com.banco.app.domain.Cliente;
import com.banco.app.dto.request.ClienteRequestDTO;
import com.banco.app.dto.response.ClienteResponseDTO;
import com.banco.app.exception.ResourceNotFoundException;
import com.banco.app.mapper.ClienteMapper;
import com.banco.app.repository.ClienteRepository;
import com.banco.app.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteMapper clienteMapper = new ClienteMapper();

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteServiceImpl(clienteRepository, clienteMapper);
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setDireccion("Calle 1");
        cliente.setTelefono("0999123456");
        cliente.setClienteId("juan123");
        cliente.setContrasena("pass");
        cliente.setEstado(true);

        requestDTO = new ClienteRequestDTO(
                "Juan",
                "Perez",
                "Calle 1",
                "0999123456",
                "juan123",
                "pass",
                true);
    }

    @Test
    void findAll_ShouldReturnList() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<ClienteResponseDTO> response = clienteService.findAll();

        assertFalse(response.isEmpty());
        assertEquals("Juan", response.get(0).nombre());
        assertEquals("Perez", response.get(0).apellido());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO response = clienteService.findById(1L);

        assertNotNull(response);
        assertEquals("Juan", response.nombre());
        assertEquals("Perez", response.apellido());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clienteService.findById(1L));
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void create_ShouldReturnCreatedCliente() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO response = clienteService.create(requestDTO);

        assertNotNull(response);
        assertEquals("Juan", response.nombre());
        assertEquals("Perez", response.apellido());
        assertEquals("juan123", response.clienteId());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void update_ShouldReturnUpdatedCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO response = clienteService.update(1L, requestDTO);

        assertNotNull(response);
        assertEquals("Juan", response.nombre());
        assertEquals("Perez", response.apellido());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void delete_ShouldCallDelete() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        assertDoesNotThrow(() -> clienteService.delete(1L));
        verify(clienteRepository, times(1)).deleteById(1L);
    }
}

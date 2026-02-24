package com.banco.app.controller;

import com.banco.app.domain.Movimiento.TipoMovimiento;

import com.banco.app.dto.request.MovimientoRequestDTO;
import com.banco.app.dto.response.MovimientoResponseDTO;
import com.banco.app.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoResponseDTO>> findAll() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/tipos")
    public ResponseEntity<TipoMovimiento[]> getTipos() {
        return ResponseEntity.ok(TipoMovimiento.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<MovimientoResponseDTO>> findByCuentaId(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoService.findByCuentaId(cuentaId));
    }

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> create(@Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.ok(movimientoService.update(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> patch(@PathVariable Long id,
            @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.ok(movimientoService.patch(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

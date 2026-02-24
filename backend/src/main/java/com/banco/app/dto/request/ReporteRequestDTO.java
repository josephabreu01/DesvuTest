package com.banco.app.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReporteRequestDTO(
                String clienteId,

                String nombreCliente,

                @NotNull(message = "La fecha de inicio es obligatoria") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

                @NotNull(message = "La fecha de fin es obligatoria") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
}

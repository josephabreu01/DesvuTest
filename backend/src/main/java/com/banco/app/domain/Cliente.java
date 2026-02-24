package com.banco.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Persona {

    @Column(name = "cliente_id", nullable = false, unique = true, updatable = false)
    private String clienteId;

    @PrePersist
    protected void onCreate() {
        if (this.clienteId == null || this.clienteId.trim().isEmpty()) {
            this.clienteId = java.util.UUID.randomUUID().toString();
        }
    }

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private Boolean estado = true;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;
}

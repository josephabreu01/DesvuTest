package com.banco.app.repository;

import com.banco.app.domain.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "cuenta" })
    List<Movimiento> findAll();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "cuenta" })
    List<Movimiento> findByCuentaId(Long cuentaId);

    List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin);

    List<Movimiento> findByCuentaClienteClienteIdAndFechaBetween(String clienteId, LocalDate fechaInicio,
            LocalDate fechaFin);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(ABS(m.valor)), 0) FROM Movimiento m WHERE m.cuenta.cliente.clienteId = :clienteId AND m.fecha = :fecha AND UPPER(m.tipoMovimiento) = 'RETIRO'")
    java.math.BigDecimal sumRetirosDia(String clienteId, LocalDate fecha);
}

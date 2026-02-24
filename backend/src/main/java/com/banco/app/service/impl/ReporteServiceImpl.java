package com.banco.app.service.impl;

import com.banco.app.domain.Cliente;
import com.banco.app.domain.Movimiento;
import com.banco.app.dto.response.ReporteDTO;
import com.banco.app.dto.request.ReporteRequestDTO;
import com.banco.app.exception.ResourceNotFoundException;
import com.banco.app.repository.ClienteRepository;
import com.banco.app.repository.MovimientoRepository;
import com.banco.app.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public List<ReporteDTO> generateReport(ReporteRequestDTO request) {
        List<Cliente> clientes = new ArrayList<>();

        if (request.clienteId() != null && !request.clienteId().isBlank()) {
            clientes.add(clienteRepository.findByClienteId(request.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cliente no encontrado con clienteId: " + request.clienteId())));
        } else if (request.nombreCliente() != null && !request.nombreCliente().isBlank()) {
            clientes = clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                    request.nombreCliente(), request.nombreCliente());
            if (clientes.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No se encontraron clientes con el nombre: " + request.nombreCliente());
            }
        } else {
            throw new IllegalArgumentException("Debe proporcionar clienteId o nombreCliente");
        }

        List<ReporteDTO> allReportes = new ArrayList<>();

        for (Cliente cliente : clientes) {
            List<Movimiento> movimientos = movimientoRepository
                    .findByCuentaClienteClienteIdAndFechaBetween(cliente.getClienteId(), request.fechaInicio(),
                            request.fechaFin());

            // Group movements by account
            Map<Long, List<Movimiento>> movimientosPorCuenta = movimientos.stream()
                    .collect(Collectors.groupingBy(m -> m.getCuenta().getId()));

            if (movimientosPorCuenta.isEmpty()) {
                if (cliente.getCuentas() != null) {
                    cliente.getCuentas().forEach(cuenta -> {
                        allReportes.add(new ReporteDTO(
                                cliente.getNombre(),
                                cliente.getApellido(),
                                cliente.getClienteId(),
                                cuenta.getNumeroCuenta(),
                                cuenta.getTipoCuenta(),
                                cuenta.getSaldoInicial(),
                                cuenta.getEstado(),
                                List.of()));
                    });
                }
            } else {
                movimientosPorCuenta.forEach((cuentaId, movs) -> {
                    var cuenta = movs.get(0).getCuenta();
                    List<ReporteDTO.MovimientoDetalleDTO> detalles = movs.stream()
                            .map(m -> new ReporteDTO.MovimientoDetalleDTO(
                                    m.getFecha(),
                                    m.getTipoMovimiento(),
                                    m.getValor(),
                                    m.getSaldo()))
                            .collect(Collectors.toList());

                    allReportes.add(new ReporteDTO(
                            cliente.getNombre(),
                            cliente.getApellido(),
                            cliente.getClienteId(),
                            cuenta.getNumeroCuenta(),
                            cuenta.getTipoCuenta(),
                            cuenta.getSaldoInicial(),
                            cuenta.getEstado(),
                            detalles));
                });
            }
        }

        return allReportes;
    }

    @Override
    public String generateBase64PdfReport(ReporteRequestDTO request) {
        List<ReporteDTO> reportes = generateReport(request);

        try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
            com.lowagie.text.pdf.PdfWriter.getInstance(document, out);

            document.open();

            // Title
            com.lowagie.text.Font fontTitle = com.lowagie.text.FontFactory
                    .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(18);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Estado de Cuenta", fontTitle);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.lowagie.text.Paragraph(" "));

            // Table
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);

            String[] headers = { "Fecha", "Cliente", "Numero Cuenta", "Tipo", "Saldo Inicial", "Estado", "Movimiento",
                    "Saldo Disponible" };
            for (String header : headers) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                        new com.lowagie.text.Phrase(header));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (ReporteDTO r : reportes) {
                for (ReporteDTO.MovimientoDetalleDTO m : r.movimientos()) {
                    table.addCell(m.fecha().toString());
                    table.addCell(r.clienteNombre() + " " + r.clienteApellido());
                    table.addCell(r.numeroCuenta());
                    table.addCell(r.tipoCuenta());
                    table.addCell("$" + m.saldo().subtract(m.valor()).toString());
                    table.addCell(r.estadoCuenta() ? "True" : "False");
                    table.addCell(m.valor().toString());
                    table.addCell("$" + m.saldo().toString());
                }
            }

            document.add(table);
            document.close();

            return java.util.Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}

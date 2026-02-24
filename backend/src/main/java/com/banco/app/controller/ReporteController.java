package com.banco.app.controller;

import com.banco.app.dto.response.ReporteDTO;
import com.banco.app.dto.request.ReporteRequestDTO;
import com.banco.app.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteDTO>> getReporte(@Valid ReporteRequestDTO request) {
        return ResponseEntity.ok(reporteService.generateReport(request));
    }

    @GetMapping("/pdf")
    public ResponseEntity<String> getReportePdf(@Valid ReporteRequestDTO request) {
        return ResponseEntity.ok(reporteService.generateBase64PdfReport(request));
    }
}

package com.banco.app.service;

import com.banco.app.dto.response.ReporteDTO;
import com.banco.app.dto.request.ReporteRequestDTO;

import java.util.List;

public interface ReporteService {
    List<ReporteDTO> generateReport(ReporteRequestDTO request);

    String generateBase64PdfReport(ReporteRequestDTO request);
}

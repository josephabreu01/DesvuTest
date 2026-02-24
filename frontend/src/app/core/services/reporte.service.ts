import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reporte } from '../models/reporte.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReporteService {
    private apiUrl = `${environment.apiUrl}/reportes`;

    constructor(private http: HttpClient) { }

    getReporte(clienteId: string, nombreCliente: string, fechaInicio: string, fechaFin: string): Observable<Reporte[]> {
        const query = `clienteId=${clienteId}&nombreCliente=${nombreCliente}&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
        return this.http.get<Reporte[]>(`${this.apiUrl}?${query}`);
    }

    getReportePdf(clienteId: string, nombreCliente: string, fechaInicio: string, fechaFin: string): Observable<string> {
        const query = `clienteId=${clienteId}&nombreCliente=${nombreCliente}&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
        return this.http.get(`${this.apiUrl}/pdf?${query}`, { responseType: 'text' });
    }
}

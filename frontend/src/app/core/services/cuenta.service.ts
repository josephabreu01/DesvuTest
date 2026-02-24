import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cuenta, CuentaRequest } from '../models/cuenta.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CuentaService {
    private apiUrl = `${environment.apiUrl}/cuentas`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Cuenta[]> {
        return this.http.get<Cuenta[]>(this.apiUrl);
    }

    getById(id: number): Observable<Cuenta> {
        return this.http.get<Cuenta>(`${this.apiUrl}/${id}`);
    }

    getByClienteId(clienteId: number): Observable<Cuenta[]> {
        return this.http.get<Cuenta[]>(`${this.apiUrl}/cliente/${clienteId}`);
    }

    create(cuenta: CuentaRequest): Observable<Cuenta> {
        return this.http.post<Cuenta>(this.apiUrl, cuenta);
    }

    update(id: number, cuenta: CuentaRequest): Observable<Cuenta> {
        return this.http.put<Cuenta>(`${this.apiUrl}/${id}`, cuenta);
    }

    patch(id: number, cuenta: Partial<CuentaRequest>): Observable<Cuenta> {
        return this.http.patch<Cuenta>(`${this.apiUrl}/${id}`, cuenta);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}

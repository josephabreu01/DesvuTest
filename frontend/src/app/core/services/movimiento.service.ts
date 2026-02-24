import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Movimiento, MovimientoRequest, TipoMovimiento } from '../models/movimiento.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MovimientoService {
    private apiUrl = `${environment.apiUrl}/movimientos`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Movimiento[]> {
        return this.http.get<Movimiento[]>(this.apiUrl);
    }

    getTipos(): Observable<TipoMovimiento[]> {
        return this.http.get<TipoMovimiento[]>(`${this.apiUrl}/tipos`);
    }

    getById(id: number): Observable<Movimiento> {
        return this.http.get<Movimiento>(`${this.apiUrl}/${id}`);
    }

    getByCuentaId(cuentaId: number): Observable<Movimiento[]> {
        return this.http.get<Movimiento[]>(`${this.apiUrl}/cuenta/${cuentaId}`);
    }

    create(movimiento: MovimientoRequest): Observable<Movimiento> {
        return this.http.post<Movimiento>(this.apiUrl, movimiento);
    }

    update(id: number, movimiento: MovimientoRequest): Observable<Movimiento> {
        return this.http.put<Movimiento>(`${this.apiUrl}/${id}`, movimiento);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}

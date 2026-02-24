import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente, ClienteRequest } from '../models/cliente.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ClienteService {
    private apiUrl = `${environment.apiUrl}/clientes`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<Cliente[]> {
        return this.http.get<Cliente[]>(this.apiUrl);
    }

    getById(id: number): Observable<Cliente> {
        return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
    }

    getByClienteId(clienteId: string): Observable<Cliente> {
        return this.http.get<Cliente>(`${this.apiUrl}/clienteId/${clienteId}`);
    }

    create(cliente: ClienteRequest): Observable<Cliente> {
        return this.http.post<Cliente>(this.apiUrl, cliente);
    }

    update(id: number, cliente: ClienteRequest): Observable<Cliente> {
        return this.http.put<Cliente>(`${this.apiUrl}/${id}`, cliente);
    }

    patch(id: number, cliente: Partial<ClienteRequest>): Observable<Cliente> {
        return this.http.patch<Cliente>(`${this.apiUrl}/${id}`, cliente);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
